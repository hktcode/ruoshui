/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

public class BgMethodResultNormalInfo<C, M, T extends SimpleBgWorker<T>> //
    implements BgMethodGetResult<T>
{
    public static <C, M, T extends SimpleBgWorker<T>> //
    BgMethodResultNormalInfo<C, M, T> of(C configs, M metrics)
    {
        if (configs == null) {
            throw new ArgumentNullException("config");
        }
        if (metrics == null) {
            throw new ArgumentNullException("metric");
        }
        return new BgMethodResultNormalInfo<>(configs, metrics);
    }

    public final C configs;

    public final M metrics;

    private BgMethodResultNormalInfo(C configs, M metrics)
    {
        this.configs = configs;
        this.metrics = metrics;
    }
}
