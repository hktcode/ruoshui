/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class SimpleDelFailureBgParams<T extends SimpleBasicBgWorker<T>> //
    implements SimpleBasicDelBgParams<T>
{
    public static <T extends SimpleBasicBgWorker<T>> //
    SimpleDelFailureBgParams<T> of(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return new SimpleDelFailureBgParams<>(reasons, endtime);
    }

    public final Throwable reasons;

    public final ZonedDateTime endtime;

    private SimpleDelFailureBgParams(Throwable reasons, ZonedDateTime endtime)
    {
        this.reasons = reasons;
        this.endtime = endtime;
    }

    @Override
    public SimpleBasicDelBgResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.del(this.reasons, this.endtime);
    }

    @Override
    public SimpleBasicDelBgResult<T> //
    run(T worker, Throwable reasons, ZonedDateTime endtime)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return worker.del(this.reasons, this.endtime);
    }
}
