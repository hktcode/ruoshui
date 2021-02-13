package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.simple.SimpleActionRun;
import com.hktcode.simple.SimpleThread;

public class UppdcThread extends SimpleThread<UppdcConfig, UppdcMetric, UpperHolder>
{
    public static UppdcThread of(UppdcConfig config, UppdcMetric metric, UpperHolder holder)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new UppdcThread(config, metric, holder);
    }

    private UppdcThread(UppdcConfig config, UppdcMetric metric, UpperHolder holder)
    {
        super(config, metric, holder);
    }
}
