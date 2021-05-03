package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public class UpcsmWorker extends SimpleWorker<UpcsmConfig, UpcsmMeters, UpperExesvc>
{
    public static UpcsmWorker of(UpcsmConfig config, UpcsmMeters meters, UpperExesvc exesvc)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        return new UpcsmWorker(config, meters, exesvc);
    }

    private UpcsmWorker(UpcsmConfig config, UpcsmMeters meters, UpperExesvc exesvc)
    {
        super(config, meters, exesvc);
    }

    @Override
    public UpcsmActionRun action()
    {
        return UpcsmActionRun.of(this.config);
    }
}
