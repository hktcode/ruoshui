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

    public <W extends SimpleWorker<W, M>, M> //
    void newStatus(W worker, M metric) throws InterruptedException
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        this.setResult(worker, metric);
        int phase = this.phaser.arriveAndDeregister();
        this.phaser.awaitAdvanceInterruptibly(phase);
    }

    public abstract <W extends SimpleWorker<W, M>, M> void setResult(W worker, M metric);

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
