/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

public interface SimpleStatus<W, M>
{
    SimpleStatus<W, M> get();

    SimpleStatus<W, M> pst();

    SimpleStatus<W, M> del();
}
