/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.datatype;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThreadBasic;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerConfig;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperDatatypeThread extends UpperConsumerThreadBasic<UpperDatatypeRecord>
{
    public static UpperDatatypeThread of
        /* */( UpperConsumerConfig config //
        /* */, PgConnection pgrepl //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("thread");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("metric");
        }
        if (status == null) {
            throw new ArgumentNullException("tqueue");
        }
        TransferQueue<UpperDatatypeRecord> tqueue = new LinkedTransferQueue<>();
        UpperDatatypeSender sender = UpperDatatypeSender.of(tqueue, status);
        Thread thread = new Thread(UpperDatatype.of(config, pgrepl, sender));
        thread.start();
        return new UpperDatatypeThread(thread, tqueue);
    }

    @Override
    public UpperConsumerRecord //
    poll(long timeout, UpperConsumerMutableMetric metric)
        throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        UpperDatatypeRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            return record.update(metric);
        }
        else if (this.thread.isAlive()) {
            return null;
        }
        else {
            // TODO: throw new DelegateNotAliveException();
            throw new RuntimeException();
        }
    }

    private UpperDatatypeThread
        /* */( Thread thread //
        /* */, TransferQueue<UpperDatatypeRecord> tqueue //
        /* */)
    {
        super(thread, tqueue);
    }
}
