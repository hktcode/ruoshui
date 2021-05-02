package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public class UpcsmWorker extends SimpleWorker<UpcsmConfig, UpcsmMetric, UpperExesvc>
{
    public static UpcsmWorker of(UpcsmConfig config, UpcsmMetric metric, UpperExesvc exesvc)
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
        return new UpcsmWorker(config, metric, exesvc);
    }

    private UpcsmWorker(UpcsmConfig config, UpcsmMetric metric, UpperExesvc exesvc)
    {
        super(config, metric, exesvc);
    }

    @Override
    public UpcsmActionRun action()
    {
        return UpcsmActionRun.of();
    }
}
