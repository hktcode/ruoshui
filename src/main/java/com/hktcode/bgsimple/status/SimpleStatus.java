/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

public interface SimpleStatus
{
    SimpleStatusOuter outer(SimpleStatusOuter outer);
    SimpleStatusInner inner() throws InterruptedException;
}
