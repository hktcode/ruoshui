/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethodPst<T extends SimpleBgWorker<T>> extends BgMethod<T>
{
    @Override
    BgMethodPstResult<T> run(T worker);

    @Override
    BgMethodPstResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}