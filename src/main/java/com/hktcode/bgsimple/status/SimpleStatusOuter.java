/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.method.SimpleMethod;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;

public abstract class SimpleStatusOuter implements SimpleStatus
{
    private static final Logger logger = LoggerFactory.getLogger(SimpleStatusOuter.class);

    public final Phaser phaser;

    public final SimpleMethod<?>[] method;

    protected SimpleStatusOuter(Phaser phaser, SimpleMethod<?>[] method)
    {
        this.phaser = phaser;
        this.method = method;
    }

    public <A extends BgWorker<A>> void newStatus(A wkstep, int number) //
        throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        @SuppressWarnings("unchecked")
        SimpleMethod<A> w = (SimpleMethod<A>) this.method[number];
        this.method[number] = w.run(wkstep);
        int phase = this.phaser.arriveAndDeregister();
        if (phase < 0) {
            logger.error("phaser.arriveAndDeregister: number={}, phaser={}", number, phase);
        }
        this.phaser.awaitAdvanceInterruptibly(phase);
        this.phaser.awaitAdvanceInterruptibly(this.phaser.getPhase());
    }

    @Override
    public SimpleStatus get(SimpleStatusOuterGet get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        return this;
    }

    @Override
    public SimpleStatus pst(SimpleStatusOuterPst pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        return this;
    }

    @Override
    public SimpleStatus del(SimpleStatusOuterDel del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        return this;
    }
}
