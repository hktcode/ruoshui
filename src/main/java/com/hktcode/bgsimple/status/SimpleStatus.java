/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.future.SimpleFuture;

import java.util.concurrent.atomic.AtomicReference;

public interface SimpleStatus
{
    SimpleStatusOuter get(SimpleStatusOuterGet get);

    SimpleStatusOuter pst(SimpleStatusOuterPst pst);

    SimpleStatusOuter del(SimpleStatusOuterDel del);

    SimpleFuture newFuture(AtomicReference<SimpleStatus> status);
}
