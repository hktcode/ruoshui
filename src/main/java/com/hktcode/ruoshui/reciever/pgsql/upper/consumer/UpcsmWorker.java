package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public class UpcsmWorker extends SimpleWorker<UpcsmWorkerArgval, UpcsmWorkerMeters, UpperExesvc>
{
    public static UpcsmWorker of(UpcsmWorkerArgval argval, UpcsmWorkerMeters meters, UpperExesvc exesvc)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        return new UpcsmWorker(argval, meters, exesvc);
    }

    private UpcsmWorker(UpcsmWorkerArgval argval, UpcsmWorkerMeters meters, UpperExesvc exesvc)
    {
        super(argval, meters, exesvc);
    }

    @Override
    public UpcsmWkstepAction action()
    {
        return this.argval.actionInfos.get(0).action();
    }
}
