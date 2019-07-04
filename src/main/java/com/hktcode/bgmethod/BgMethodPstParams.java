/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethodPstParams<T extends SimpleBgWorker<T>> //
    extends BgMethodPst<T>, BgMethodParams<T>
{
    @Override
    BgMethodPstResult<T> run(T worker);

    @Override
    BgMethodPstResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}
