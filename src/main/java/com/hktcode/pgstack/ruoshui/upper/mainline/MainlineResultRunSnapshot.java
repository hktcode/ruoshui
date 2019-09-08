/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.bgsimple.method.SimpleMethodAllResultRun;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineResultRunSnapshot extends MainlineResultRun //
{
    public static MainlineResultRunSnapshot of(MainlineConfig config, MainlineMetric metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metrci");
        }
        return new MainlineResultRunSnapshot(config, metric);
    }

    private MainlineResultRunSnapshot(MainlineConfig config, MainlineMetric metric)
    {
        super(config, metric);
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode config = node.putObject("config");
        this.config.toJsonObject(config);
        ObjectNode metric = node.putObject("metric");
        this.metric.toJsonObject(metric);
    }
}
