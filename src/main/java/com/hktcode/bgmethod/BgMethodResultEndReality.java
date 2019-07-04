/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public abstract class BgMethodResultEndReality<C, M, T extends SimpleBasicBgWorker<T>> //
    extends BgMethodResultEndAlready<T>
{
    public final C configs;

    public final M metrics;

    protected BgMethodResultEndReality(C configs, M metrics, ZonedDateTime endtime)
    {
        super(endtime);
        this.configs = configs;
        this.metrics = metrics;
    }
}
