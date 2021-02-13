/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.simple.SimpleActionRun;

abstract class UppdcActionRun extends SimpleActionRun<UppdcConfig, UppdcMetric, UpperHolder>
{
    protected UppdcActionRun(UppdcConfig config, UppdcMetric metric, UpperHolder holder)
    {
        super(config, metric, holder);
    }
}
