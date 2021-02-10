/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.triple.Triple;
import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordProducer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Upjct extends Triple
{
    public static Upjct of //
        /* */(TripleJunctionConfig config //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */, BlockingQueue<UpperRecordProducer> getout //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (comein == null) {
            throw new ArgumentNullException("comein");
        }
        if (getout == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new Upjct(config, comein, getout, status);
    }

    private final TripleJunctionConfig config;

    private final BlockingQueue<UpperRecordConsumer> comein;

    private final BlockingQueue<UpperRecordProducer> getout;

    private Upjct //
        /* */( TripleJunctionConfig config //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */, BlockingQueue<UpperRecordProducer> getout //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(status, 1);
        this.config = config;
        this.comein = comein;
        this.getout = getout;
    }

    @Override
    protected UpjctActionRun createsAction()
    {
        return UpjctActionRun.of(config, comein, getout, status);
    }
}
