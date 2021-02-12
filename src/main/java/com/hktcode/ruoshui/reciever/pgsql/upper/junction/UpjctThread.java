package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.simple.SimpleActionRun;
import com.hktcode.simple.SimpleThread;

public class UpjctThread extends SimpleThread<UpjctConfig, UpjctMetric, UpperHolder>
{
    public static UpjctThread of(UpjctConfig config, UpjctMetric metric, UpperHolder holder)
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
        return new UpjctThread(config, metric, holder);
    }

    private UpjctThread(UpjctConfig config, UpjctMetric metric, UpperHolder holder)
    {
        super(config, metric, holder);
    }

    @Override
    public SimpleActionRun<UpjctConfig, UpjctMetric, UpperHolder> createAction()
    {
        return UpjctActionRun.of(this.config, this.metric, this.entity);
    }
}
