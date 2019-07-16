/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.method.SimpleMethodGet;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusOuterGet extends SimpleStatusOuter
{
    public static SimpleStatusOuterGet of(SimpleMethodGet[] method, Phaser phaser)
    {
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        if (phaser == null) {
            throw new ArgumentNullException("phaser");
        }
        return new SimpleStatusOuterGet(method, phaser);
    }

    private final SimpleMethodGet[] method;

    private SimpleStatusOuterGet(SimpleMethodGet[] method, Phaser phaser)
    {
        super(phaser);
        this.method = method;
    }

    @Override
    public <W extends SimpleWorker<W>> void setResult(W wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        int index = wkstep.number;
        @SuppressWarnings("unchecked")
        SimpleMethodGet<W> w = (SimpleMethodGet<W>) this.method[index];
        this.method[index] = w.run(wkstep);
    }
}
