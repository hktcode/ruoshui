/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgResultUnk implements PgResult
{
    public static PgResultUnk of(PgConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new PgResultUnk(config);
    }

    public final PgConfig config;

    public final PgMetricUnk metric;

    private PgResultUnk(PgConfig config)
    {
        this.config = config;
        this.metric = PgMetricUnk.of();
    }
}
