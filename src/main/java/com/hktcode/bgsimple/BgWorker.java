/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.bgsimple.method.*;

public interface BgWorker
{
    SimpleMethodResult pst() throws InterruptedException;

    SimpleMethodResult put() throws InterruptedException;

    SimpleMethodResult get() throws InterruptedException;

    SimpleMethodResult del() throws InterruptedException;
}
