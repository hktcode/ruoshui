/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.method.SimpleMethodDel;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusOuterDel extends SimpleStatusOuter
{
    public static SimpleStatusOuterDel of(SimpleMethodDel<?>[] method, Phaser phaser)
    {
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        if (phaser == null) {
            throw new ArgumentNullException("phaser");
        }
        return new SimpleStatusOuterDel(method, phaser);
    }

    private SimpleStatusOuterDel(SimpleMethodDel<?>[] method, Phaser phaser)
    {
        super(phaser, method);
    }
}
