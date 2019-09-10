/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgResultNormalSnapshot extends PgResultNormal
{
    static PgResultNormalSnapshot of(PgConfig config, PgMetricRun metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgResultNormalSnapshot(config, metric);
    }

    private PgResultNormalSnapshot(PgConfig config, PgMetricRun metric)
    {
        super(config, metric);
    }
}
