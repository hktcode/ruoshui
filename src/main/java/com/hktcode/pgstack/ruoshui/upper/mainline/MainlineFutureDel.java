/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.bgsimple.status.SimpleStatusOuterDel;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class MainlineFutureDel extends MainlineFuture<SimpleStatusOuterDel>
{
    public static //
    MainlineFutureDel of(AtomicReference<SimpleStatus> status, SimpleStatusOuterDel origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new MainlineFutureDel(status, origin);
    }

    private MainlineFutureDel //
        (AtomicReference<SimpleStatus> status, SimpleStatusOuterDel origin)
    {
        super(status, origin);
    }

    @Override
    protected MainlineResult getResult()
    {
        return super.getResult(origin.method[0]);
    }
}
