/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public abstract class SimpleStatusOuter implements SimpleStatus
{
    public final Phaser phaser;

    protected SimpleStatusOuter(Phaser phaser)
    {
        this.phaser = phaser;
    }

    public <A extends BgWorker<A>> void newStatus(A wkstep, int number) //
        throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        this.setResult(wkstep, number);
        int phase = this.phaser.arriveAndDeregister();
        this.phaser.awaitAdvanceInterruptibly(phase);
        this.phaser.awaitAdvanceInterruptibly(this.phaser.getPhase());
    }

    public abstract <A extends BgWorker<A>> //
    void setResult(A worker, int number);

    public SimpleStatus outer(SimpleStatus outer)
    {
        if (outer == null) {
            throw new ArgumentNullException("outer");
        }
        return this;
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
