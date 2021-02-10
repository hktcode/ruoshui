/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.method.SimpleMethodPst;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusOuterPst extends SimpleStatusOuter
{
    public static SimpleStatusOuterPst of(SimpleMethodPst<?>[] method, Phaser phaser)
    {
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        if (phaser == null) {
            throw new ArgumentNullException("phaser");
        }
        return new SimpleStatusOuterPst(method, phaser);
    }

    private SimpleStatusOuterPst(SimpleMethodPst<?>[] method, Phaser phaser)
    {
        super(phaser, method);
    }
}
