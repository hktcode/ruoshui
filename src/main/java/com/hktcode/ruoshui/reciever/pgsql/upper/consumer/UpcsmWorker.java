package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;

public class UpcsmWorker extends SimpleWorker<UpcsmWorkerArgval, UpcsmWorkerMeters>
{
    public static UpcsmWorker of(UpcsmWorkerArgval argval, UpcsmWorkerMeters meters, SimpleAtomic holder)
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
        return new UpcsmWorker(argval, meters, holder);
    }

    private UpcsmWorker(UpcsmWorkerArgval argval, UpcsmWorkerMeters meters, SimpleAtomic holder)
    {
        super(argval, meters, holder);
    }
}
