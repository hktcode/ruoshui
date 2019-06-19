/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleNormalInfoBgResult<C, M, T extends SimpleBasicBgWorker<T>> //
    implements SimpleBasicGetBgResult<T>
{
    public static <C, M, T extends SimpleBasicBgWorker<T>> //
    SimpleNormalInfoBgResult<C, M, T> of(C configs, M metrics)
    {
        if (configs == null) {
            throw new ArgumentNullException("config");
        }
        if (metrics == null) {
            throw new ArgumentNullException("metric");
        }
        return new SimpleNormalInfoBgResult<>(configs, metrics);
    }

    public final C configs;

    public final M metrics;

    private SimpleNormalInfoBgResult(C configs, M metrics)
    {
        this.configs = configs;
        this.metrics = metrics;
    }
}
