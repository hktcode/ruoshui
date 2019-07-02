/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import java.time.ZonedDateTime;

public abstract class SimpleRealityEndBgResult<C, M, T extends SimpleBasicBgWorker<T>> //
    extends SimpleAlreadyEndBgResult<T>
{
    public final C configs;

    public final M metrics;

    protected SimpleRealityEndBgResult(C configs, M metrics, ZonedDateTime endtime)
    {
        super(endtime);
        this.configs = configs;
        this.metrics = metrics;
    }
}
