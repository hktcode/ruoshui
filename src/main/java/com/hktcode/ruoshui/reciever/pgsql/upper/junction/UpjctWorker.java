package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public class UpjctWorker extends SimpleWorker<UpjctWorkerArgval, UpjctWorkerMeters, UpperExesvc>
{
    public static UpjctWorker of(UpjctWorkerArgval config, UpjctWorkerMeters meters, UpperExesvc exesvc)
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
        return new UpjctWorker(config, meters, exesvc);
    }

    private UpjctWorker(UpjctWorkerArgval config, UpjctWorkerMeters meters, UpperExesvc exesvc)
    {
        super(config, meters, exesvc);
    }

    public UpjctWkstepAction action()
    {
        return this.argval.actionInfos.get(0).action();
    }
}
