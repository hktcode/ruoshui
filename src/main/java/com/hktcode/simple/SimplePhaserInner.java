/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimplePhaserInner implements SimplePhaser
{
    public static SimplePhaserInner of(long deletets)
    {
        return new SimplePhaserInner(deletets);
    }

    public final long deletets;

    private SimplePhaserInner(long deletets)
    {
        this.deletets = deletets;
    }

    public SimplePhaserOuter cmd(SimplePhaserOuter cmd)
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        return this.deletets == Long.MAX_VALUE ? cmd : SimplePhaserOuter.of(1);
    }
}
