/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.triple.Triple;
import com.hktcode.bgsimple.triple.TripleActionRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordProducer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Uppdc extends Triple
{
    public static Uppdc of //
        /* */(UppdcConfig config //
        /* */, BlockingQueue<UpperRecordProducer> getout //
        /* */, AtomicReference<SimpleStatus> status //
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
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(status, 2);
        this.config = config;
        this.getout = getout;
    }

    @Override
    protected TripleActionRun createsAction()
    {
        return UppdcActionRun.of(config, getout, status);
    }
}
