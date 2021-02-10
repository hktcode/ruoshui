/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.triple.Triple;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;

import java.util.concurrent.BlockingQueue;

public class Uppdc extends Triple<UppdcConfig, UppdcMetricRun>
{
    public static Uppdc of //
        /* */(UppdcConfig config //
        /* */, BlockingQueue<UpperRecordProducer> getout //
        /* */, SimpleHolder status //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (getout == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new Uppdc(config, getout, status);
    }

    private final UppdcConfig config;

    private final BlockingQueue<UpperRecordProducer> getout;

    private Uppdc //
        /* */(UppdcConfig config //
        /* */, BlockingQueue<UpperRecordProducer> getout //
        /* */, SimpleHolder status //
        /* */)
    {
        super(status, 2);
        this.config = config;
        this.getout = getout;
    }

    @Override
    protected UppdcActionRun createsAction()
    {
        return UppdcActionRun.of(config, getout, status);
    }
}
