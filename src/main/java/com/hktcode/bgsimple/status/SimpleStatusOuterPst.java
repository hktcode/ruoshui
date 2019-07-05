/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.method.SimpleMethodPst;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusOuterPst extends SimpleStatusOuter
{
    public static SimpleStatusOuterPst of(SimpleMethodPst[] method, Phaser phaser)
    {
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        if (phaser == null) {
            throw new ArgumentNullException("phaser");
        }
        return new SimpleStatusOuterPst(method, phaser);
    }

    private final SimpleMethodPst[] method;

    private SimpleStatusOuterPst(SimpleMethodPst[] method, Phaser phaser)
    {
        super(phaser);
        this.method = method;
    }

    @Override
    public <W extends SimpleWorker<W, M>, M> void setResult(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        int index = worker.number;
        @SuppressWarnings("unchecked")
        SimpleMethodPst<W, M> w = (SimpleMethodPst<W, M>) this.method[index];
        this.method[index] = w.run(worker, metric);
    }
}
