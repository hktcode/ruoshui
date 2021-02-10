package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.simple.SimpleEntity;
import com.hktcode.simple.SimpleHolder;
import com.hktcode.simple.SimpleActionRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperEntity;

public class UppdcEntity extends SimpleEntity<UppdcConfig, UppdcMetric, UpperEntity>
{
    public static UppdcEntity of(UppdcConfig config, UppdcMetric metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UppdcEntity(config, metric);
    }

    private UppdcEntity(UppdcConfig config, UppdcMetric metric)
    {
        super(config, metric);
    }

    @Override
    public SimpleActionRun<UpperEntity> createAction(SimpleHolder<UpperEntity> holder)
    {
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return UppdcActionRun.of(holder);
    }
}
