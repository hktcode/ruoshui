/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.BgWorker;
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

    public final SimpleMethodPst[] method;

    private SimpleStatusOuterPst(SimpleMethodPst[] method, Phaser phaser)
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
        SimpleMethodPst<A> w = (SimpleMethodPst<A>) this.method[number];
        this.method[number] = w.run(wkstep);
    }
}
