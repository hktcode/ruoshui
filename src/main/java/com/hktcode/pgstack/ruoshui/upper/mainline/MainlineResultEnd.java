/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineResultEnd extends MainlineResult<MainlineMetricEnd> //
    implements SimpleMethodAllResultEnd<MainlineAction> //
{
    public static MainlineResultEnd of(MainlineConfig config, MainlineMetricEnd metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineResultEnd(config, metric);
    }

    private MainlineResultEnd(MainlineConfig config, MainlineMetricEnd metric)
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
