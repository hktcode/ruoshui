package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;

public class UpjctWorker extends SimpleWorker<UpjctWorkerArgval, UpjctWorkerMeters>
{
    public static UpjctWorker of(UpjctWorkerArgval config, UpjctWorkerMeters meters, SimpleAtomic holder)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new UpjctWorker(config, meters, holder);
    }

    private UpjctWorker(UpjctWorkerArgval config, UpjctWorkerMeters meters, SimpleAtomic holder)
    {
        super(config, meters, holder);
    }
}
