package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.simple.SimpleThread;

public class UpcsmThread extends SimpleThread<UpcsmConfig, UpcsmMetric, UpperHolder>
{
    public static UpcsmThread of(UpcsmConfig config, UpcsmMetric metric, UpperHolder holder)
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
        return new UpcsmThread(config, metric, holder);
    }

    private UpcsmThread(UpcsmConfig config, UpcsmMetric metric, UpperHolder holder)
    {
        super(config, metric, holder);
    }

    @Override
    public UpcsmActionRun createAction()
    {
        return UpcsmActionRun.of(this.config, this.metric, this.entity);
    }
}
