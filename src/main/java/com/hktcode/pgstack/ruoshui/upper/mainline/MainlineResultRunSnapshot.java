/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderMetric;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderMetricRun;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderResultRun;

public class MainlineResultRunSnapshot extends PgsenderResultRun<MainlineRecord, MainlineConfig> //
{
    public static MainlineResultRunSnapshot of(MainlineConfig config, PgsenderMetricRun metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineResultRunSnapshot(config, metric);
    }

    private MainlineResultRunSnapshot(MainlineConfig config, PgsenderMetricRun metric)
    {
        super(config, metric);
    }
}
