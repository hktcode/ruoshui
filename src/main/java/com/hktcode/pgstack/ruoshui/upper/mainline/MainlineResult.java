/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.method.SimpleMethodAllResult;

public abstract class MainlineResult<M extends MainlineMetric>
    implements SimpleMethodAllResult<MainlineAction>
{
    public final MainlineConfig config;

    public final M metric;

    protected MainlineResult(MainlineConfig config, M metric)
    {
        this.config = config;
        this.metric = metric;
    }
}
