package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.simple.SimpleEntity;
import com.hktcode.simple.SimpleHolder;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperEntity;

public class UpcsmEntity extends SimpleEntity<UpcsmConfig, UpcsmMetric, UpperEntity>
{
    public static UpcsmEntity of(UpcsmConfig config, UpcsmMetric metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpcsmEntity(config, metric);
    }

    private UpcsmEntity(UpcsmConfig config, UpcsmMetric metric)
    {
        super(config, metric);
    }

    @Override
    public UpcsmActionRun createAction(SimpleHolder<UpperEntity> holder)
    {
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return UpcsmActionRun.of(holder);
    }
}
