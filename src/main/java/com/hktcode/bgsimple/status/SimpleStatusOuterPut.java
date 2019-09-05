/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.BgWorker;
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

    public final SimpleMethodPut[] method;

    private SimpleStatusOuterPut(SimpleMethodPut[] method, Phaser phaser)
    {
        super(phaser);
        this.method = method;
    }

    @Override
    public <A extends BgWorker<A>> void setResult(A wkstep, int number) throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        @SuppressWarnings("unchecked")
        SimpleMethodPut<A> w = (SimpleMethodPut<A>)this.method[number];
        this.method[number] = w.run(wkstep);
    }
}
