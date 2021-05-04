package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public class UppdcWorker extends SimpleWorker<UppdcWorkerArgval, UppdcWorkerMeters, UpperExesvc>
{
    public static UppdcWorker of(UppdcWorkerArgval argval, UppdcWorkerMeters meters, UpperExesvc exesvc)
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
        return new UppdcWorker(argval, meters, exesvc);
    }

    private UppdcWorker(UppdcWorkerArgval argval, UppdcWorkerMeters meters, UpperExesvc exesvc)
    {
        super(argval, meters, exesvc);
    }

    public UppdcWkstepAction action()
    {
        return this.argval.actionInfos.get(0).action();
    }
}
