/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterPst;
import com.hktcode.bgsimple.status.SimpleStatusOuterPut;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class MainlineFuturePst extends MainlineFuture<SimpleStatusOuterPst>
{
    public static //
    MainlineFuturePst of(AtomicReference<SimpleStatus> status, SimpleStatusOuterPst origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new MainlineFuturePst(status, origin);
    }

    private MainlineFuturePst //
        (AtomicReference<SimpleStatus> status, SimpleStatusOuterPst origin)
    {
        super(status, origin);
    }

    @Override
    protected MainlineResult getResult()
    {
        return super.getResult(origin.method[0]);
    }
}
