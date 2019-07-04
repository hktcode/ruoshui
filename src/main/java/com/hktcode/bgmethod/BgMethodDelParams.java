/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethodDelParams<T extends SimpleBgWorker<T>> //
    extends BgMethodDel<T>, BgMethodParams<T>
{
    @Override
    BgMethodDelResult<T> run(T worker);

    @Override
    BgMethodDelResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}
