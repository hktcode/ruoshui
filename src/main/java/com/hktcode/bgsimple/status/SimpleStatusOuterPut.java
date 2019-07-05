/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.method.SimpleMethodPut;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusOuterPut extends SimpleStatusOuter
{
    public static SimpleStatusOuterPut of(SimpleMethodPut[] method, Phaser phaser)
    {
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        if (phaser == null) {
            throw new ArgumentNullException("phaser");
        }
        return new SimpleStatusOuterPut(method, phaser);
    }

    private final SimpleMethodPut[] method;

    private SimpleStatusOuterPut(SimpleMethodPut[] method, Phaser phaser)
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
        SimpleMethodPut<W, M> w = (SimpleMethodPut<W, M>)worker;
        this.method[index] = w.run(worker, metric);
    }
}

