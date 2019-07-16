/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

class PgsqlTxactionMetricFinish extends PgsqlTxactionMetric
{
    static PgsqlTxactionMetricFinish //
    of(MainlineConfig config, PgsqlTxactionMetric metric, ZonedDateTime attime)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (attime == null) {
            throw new ArgumentNullException("attime");
        }
        return new PgsqlTxactionMetricFinish(config, metric, attime);
    }

    protected PgsqlTxactionMetricFinish //
        /* */( MainlineConfig config //
        /* */, PgsqlTxactionMetric metric //
        /* */, ZonedDateTime attime //
        /* */)
    {
        super(metric.startMillis);
        this.config = config;
        this.metric = metric;
        this.attime = attime;
    }

    final MainlineConfig config;

    final PgsqlTxactionMetric metric;

    final ZonedDateTime attime;
}
