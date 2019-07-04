/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.method.SimpleMethodDel;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusOuterDel<W extends SimpleWorker<W, M>, M> //
    extends SimpleStatusOuter<W, M>
{
    public static <W extends SimpleWorker<W, M>, M> //
    SimpleStatusOuterDel<W, M> of(SimpleMethodDel<W, M>[] method, Phaser phaser)
    {
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        if (phaser == null) {
            throw new ArgumentNullException("phaser");
        }
        return new SimpleStatusOuterDel<>(method, phaser);
    }

    private final SimpleMethodDel<W, M>[] method;

    private SimpleStatusOuterDel(SimpleMethodDel<W, M>[] method, Phaser phaser)
    {
        super(phaser);
        this.method = method;
    }

    @Override
    public void setResult(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        int index = worker.number;
        this.method[index] = this.method[index].run(worker, metric);
    }
}
