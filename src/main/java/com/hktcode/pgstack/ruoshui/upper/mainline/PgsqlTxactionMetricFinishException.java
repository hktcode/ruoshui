/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import java.time.ZonedDateTime;

public class PgsqlTxactionMetricFinishException extends PgsqlTxactionMetricFinish
{
    final Throwable reason;

    protected PgsqlTxactionMetricFinishException
        /* */( MainlineConfig config
        /* */, PgsqlTxactionMetric metric
        /* */, ZonedDateTime attime
        /* */, Throwable reason
        /* */)
    {
        super(config, metric, attime);
        this.reason = reason;
    }
}
