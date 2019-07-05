/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.bgsimple.status.SimpleStatusOuterDel;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TripleWorker //
    /* */< W extends TripleWorker<W, F, M> //
    /* */, F extends TripleConfig //
    /* */, M extends TripleMetric //
    /* */>
    extends SimpleWorker<W, M>
{
    private static final Logger logger = LoggerFactory.getLogger(TripleWorker.class);

    public final F config;

    protected TripleWorker //
        /* */( F config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, int number //
        /* */)
    {
        super(status, number);
        this.config = config;
    }

    protected abstract void runInternal(W worker, M metric) throws Exception;

    public void run(String name, W worker, M metric) throws InterruptedException
    {
        try {
            this.runInternal(worker, metric);
            logger.info("triple finish.");
        }
        catch (Exception ex) {
            logger.error("triple throws exception: ", ex);
            ZonedDateTime endtime = ZonedDateTime.now();
            String msg = ex.getMessage();
            metric.statusInfor = "throw exception at " + endtime + ": " + msg;
            @SuppressWarnings("unchecked")
            SimpleMethodDel<W, M>[] method = new SimpleMethodDel[3];
            for (int i = 0; i < method.length; ++i) {
                if (i == super.number) {
                    JsonNode c = this.config.toJsonObject();
                    JsonNode m = metric.toJsonObject();
                    method[i] = TripleMethodResult.of(c, m);
                }
                else {
                    method[i] = SimpleMethodDelParamsDefault.of();
                }
            }
            SimpleStatusOuterDel del = SimpleStatusOuterDel.of(method, new Phaser(3));
            SimpleStatus origin;
            SimpleStatus future;
            while (!((origin = super.newStatus(worker, metric)) instanceof SimpleStatusInnerEnd)) {
                future = origin.del(del);
                this.status.compareAndSet(origin, future);
                // TODO:
                metric.statusInfor = "waiting status set to end";
            }
            // TODO:
            metric.statusInfor = "consumer finish end";
            logger.info("triple finish by exception.");
        }
    }

    @Override
    public TripleMethodResult<W, F, M> put(M metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        JsonNode c = config.toJsonObject();
        JsonNode m = metric.toJsonObject();
        return TripleMethodResult.of(c, m);
    }


    @Override
    public TripleMethodResult<W, F, M> pst(M metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        JsonNode c = config.toJsonObject();
        JsonNode m = metric.toJsonObject();
        return TripleMethodResult.of(c, m);
    }

    @Override
    public TripleMethodResult<W, F, M> get(M metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        JsonNode c = config.toJsonObject();
        JsonNode m = metric.toJsonObject();
        return TripleMethodResult.of(c, m);
    }

    @Override
    public TripleMethodResult<W, F, M> del(M metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        ZonedDateTime endtime = ZonedDateTime.now(); // TODO:
        JsonNode c = config.toJsonObject();
        JsonNode m = metric.toJsonObject();
        return TripleMethodResult.of(c, m);
    }
}
