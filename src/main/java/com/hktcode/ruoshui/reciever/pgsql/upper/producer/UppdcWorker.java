package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public abstract class UppdcWorker extends SimpleWorker<UppdcConfig, UppdcMeters, UpperExesvc>
{
    protected UppdcWorker(UppdcConfig config, UppdcMeters meters, UpperExesvc exesvc)
    {
        super(config, meters, exesvc);
    }

    public abstract UppdcActionRun action();
}
