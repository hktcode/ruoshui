/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.method.SimpleMethodDelResult;
import com.hktcode.bgsimple.method.SimpleMethodGetResult;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.bgsimple.method.SimpleMethodPutResult;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.triple.Triple;
import com.hktcode.bgsimple.triple.TripleConsumer;
import com.hktcode.lang.RunnableWithInterrupted;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.ZonedDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PgsqlTxaction extends SimpleWorker<PgsqlTxaction>
    implements RunnableWithInterrupted
{
    private static final Logger logger = LoggerFactory.getLogger(PgsqlTxaction.class);

    public static PgsqlTxaction of //
        /* */( MainlineConfig config //
        /* */, TransferQueue<PgsqlTxactionRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        return new PgsqlTxaction(config, tqueue, status);
    }

    private final MainlineConfig config;

    private final TransferQueue<PgsqlTxactionRecord> tqueue;

    protected PgsqlTxaction //
        /* */( MainlineConfig config //
        /* */, TransferQueue<PgsqlTxactionRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        super(status, 1);
        this.config = config;
        this.tqueue = tqueue;
    }

    @Override
    public void runWithInterrupted() throws InterruptedException
    {
        ZonedDateTime startMillis = ZonedDateTime.now();
        PgsqlTxactionSender sender = PgsqlTxactionSender.of(tqueue, status);
        PgsqlTxactionMetric metric = PgsqlTxactionMetricDatatype.of(config, startMillis);
        try {
            metric = ((PgsqlTxactionMetricDatatype)metric).next(this);
            if (!(metric instanceof PgsqlTxactionMetricNormal)) {
                return;
            }
            try (Connection repl = config.srcProperty.replicaConnection()) {
                PgConnection pgrepl = repl.unwrap(PgConnection.class);
                do {
                    metric = ((PgsqlTxactionMetricNormal) metric).next(pgrepl, this);
                } while (metric instanceof PgsqlTxactionMetricNormal);
            }
        }
        catch (Exception ex) {
            logger.error("throws exception: ", ex);
            ZonedDateTime endtime = ZonedDateTime.now();
            metric.statusInfor = "throw exception: " + ex.getMessage();
            long timeout = config.logDuration;
            PgsqlTxactionRecord r = PgsqlTxactionRecordThrows.of(endtime, ex);
            sender.send(r, timeout, timeout, metric);
        }
    }

    @Override
    public SimpleMethodPstResult<PgsqlTxaction> pst()
    {
        return null;
    }

    @Override
    public SimpleMethodPutResult<PgsqlTxaction> put()
    {
        return null;
    }

    @Override
    public SimpleMethodGetResult<PgsqlTxaction> get()
    {
        return null;
    }

    @Override
    public SimpleMethodDelResult<PgsqlTxaction> del()
    {
        return null;
    }

    public PgsqlTxactionRecord push(PgsqlTxactionRecord record, long waitTimeout, long logDuration, PgsqlTxactionMetric metric) //
        throws InterruptedException
    {
        long startsMillis = System.currentTimeMillis();
        metric.statusInfor = "offer record wait";
        boolean success = tqueue.tryTransfer(record, waitTimeout, TimeUnit.MILLISECONDS);
        metric.statusInfor = "offer record end";
        long finishMillis = System.currentTimeMillis();
        metric.offerMillis += (finishMillis - startsMillis);
        ++metric.offerCounts;
        if (success) {
            ++metric.recordCount;
            return null;
        }
        else {
            long currMillis = System.currentTimeMillis();
            if (currMillis - metric.logDatetime >= logDuration) {
                logger.info("push record to comein fail: timeout={}, logDuration={}", waitTimeout, logDuration);
                metric.logDatetime = currMillis;
            }
            return record;
        }
    }
}
