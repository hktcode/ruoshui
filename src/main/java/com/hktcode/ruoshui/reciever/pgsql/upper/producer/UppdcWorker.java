package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public abstract class UppdcWorker<C extends UppdcConfig, M extends UppdcMetric> //
        extends SimpleWorker<C, M, UpperExesvc>
{
    protected UppdcWorker(C config, M metric, UpperExesvc exesvc)
    {
        super(config, metric, exesvc);
    }

    public abstract UppdcActionRun<C, M> action();
}
