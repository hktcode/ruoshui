/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.BgWorker;
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

    public final SimpleMethodGet[] method;

    private SimpleStatusOuterGet(SimpleMethodGet[] method, Phaser phaser)
    {
        super(phaser);
        this.method = method;
    }

    @Override
    public <A extends BgWorker<A>> void setResult(A wkstep, int number)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        @SuppressWarnings("unchecked")
        SimpleMethodGet<A> w = (SimpleMethodGet<A>) this.method[number];
        this.method[number] = w.run(wkstep);
    }
}