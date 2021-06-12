package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;

public class UppdcWorker extends SimpleWorker<UppdcWorkerArgval, UppdcWorkerMeters>
{
    public static UppdcWorker of(UppdcWorkerArgval argval, UppdcWorkerMeters meters, SimpleAtomic holder)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new UppdcWorker(argval, meters, holder);
    }

    private UppdcWorker(UppdcWorkerArgval argval, UppdcWorkerMeters meters, SimpleAtomic holder)
    {
        super(argval, meters, holder);
    }
}
