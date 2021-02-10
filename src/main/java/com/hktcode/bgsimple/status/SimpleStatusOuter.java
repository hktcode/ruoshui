/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.method.*;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;

public class SimpleStatusOuter implements SimpleStatus
{
    public static SimpleStatusOuter of(Phaser phaser, SimpleMethod[] method)
    {
        if (phaser == null) {
            throw new ArgumentNullException("phaser");
        }
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        return new SimpleStatusOuter(phaser, method);
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleStatusOuter.class);

    public final Phaser phaser;

    public final SimpleMethod[] method;

    private SimpleStatusOuter(Phaser phaser, SimpleMethod[] method)
    {
        this.phaser = phaser;
        this.method = method;
    }

    public void newStatus(BgWorker wkstep, int number) //
        throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        this.method[number] = this.method[number].run(wkstep);
        int phase = this.phaser.arriveAndDeregister();
        if (phase < 0) {
            logger.error("phaser.arriveAndDeregister: number={}, phaser={}", number, phase);
        }
        this.phaser.awaitAdvanceInterruptibly(phase);
        this.phaser.awaitAdvanceInterruptibly(this.phaser.getPhase());
    }

    public SimpleStatusOuter outer(SimpleStatusOuter outer)
    {
        if (outer == null) {
            throw new ArgumentNullException("outer");
        }
        return this;
    }

    public SimpleStatusInner inner() throws InterruptedException
    {
        phaser.awaitAdvanceInterruptibly(phaser.arrive());
        int runcount = 0;
        SimpleMethod[] originmethod = this.method;
        for (SimpleMethod simpleMethod : originmethod) {
            if (simpleMethod instanceof SimpleMethodResultRun) {
                ++runcount;
            }
        }
        SimpleStatusInner result;
        if (runcount == 0) {
            result = SimpleStatusInnerEnd.of(ImmutableList.copyOf((SimpleMethodResultEnd[])originmethod));
        } else {
            result = SimpleStatusInnerRun.of(ImmutableList.copyOf((SimpleMethodResult[])originmethod));
        }
        phaser.arriveAndDeregister();
        return result;
    }
}
