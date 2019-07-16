/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

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

    public <W extends SimpleWorker<W>> //
    void newStatus(W wkstep) throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        this.setResult(wkstep);
        int phase = this.phaser.arriveAndDeregister();
        this.phaser.awaitAdvanceInterruptibly(phase);
    }

    public abstract <W extends SimpleWorker<W>> void setResult(W worker);

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
