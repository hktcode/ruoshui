/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.future.SimpleFuture;

public interface SimpleStatus
{
    SimpleStatusOuter outer(SimpleStatusOuter outer);

    SimpleFuture newFuture(SimpleHolder status);
}
