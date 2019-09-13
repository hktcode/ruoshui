/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.triple.Triple;
import com.hktcode.bgsimple.triple.TripleActionRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Upcsm extends Triple
{
    public static Upcsm of //
        /* */( UpcsmConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (comein == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new Upcsm(config, comein, status);
    }

    private final UpcsmConfig config;

    private final BlockingQueue<UpperRecordConsumer> comein;

    private Upcsm //
        /* */( UpcsmConfig config //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(status, 0);
        this.config = config;
        this.comein = comein;
    }

    @Override
    protected TripleActionRun createsAction()
    {
        return UpcsmActionRun.of(config, comein, status);
    }
}
