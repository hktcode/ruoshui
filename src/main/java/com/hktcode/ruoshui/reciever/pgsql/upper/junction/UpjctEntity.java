package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.hktcode.simple.SimpleEntity;
import com.hktcode.simple.SimpleHolder;
import com.hktcode.simple.SimpleActionRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperEntity;

public class UpjctEntity extends SimpleEntity<UpjctConfig, UpjctMetric, UpperEntity>
{
    public static UpjctEntity of(UpjctConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new UpjctEntity(config, UpjctMetric.of());
    }

    public static UpjctEntity of(UpjctConfig config, UpjctMetric metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpjctEntity(config, metric);
    }

    private UpjctEntity(UpjctConfig config, UpjctMetric metric)
    {
        super(config, metric);
    }

    @Override
    public SimpleActionRun<UpperEntity> createAction(SimpleHolder<UpperEntity> holder)
    {
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return UpjctActionRun.of(holder);
    }
}
