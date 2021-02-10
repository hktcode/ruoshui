/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.method.SimpleMethodGet;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusOuterGet extends SimpleStatusOuter
{
    public static SimpleStatusOuterGet of(SimpleMethodGet<?>[] method, Phaser phaser)
    {
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        if (phaser == null) {
            throw new ArgumentNullException("phaser");
        }
        return new SimpleStatusOuterGet(method, phaser);
    }

    private SimpleStatusOuterGet(SimpleMethodGet<?>[] method, Phaser phaser)
    {
        super(phaser, method);
    }
}