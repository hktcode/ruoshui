package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public class UpjctWorker extends SimpleWorker<UpjctConfig, UpjctMetric, UpperExesvc>
{
    public static UpjctWorker of(UpjctConfig config, UpjctMetric metric, UpperExesvc exesvc)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        return new UpjctWorker(config, metric, exesvc);
    }

    private UpjctWorker(UpjctConfig config, UpjctMetric metric, UpperExesvc exesvc)
    {
        super(config, metric, exesvc);
    }

    public UpjctActionRun action()
    {
        return UpjctActionRun.of();
    }
}
