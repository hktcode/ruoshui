package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public class UpcsmWorker extends SimpleWorker<UpcsmArgval, UpcsmMeters, UpperExesvc>
{
    public static UpcsmWorker of(UpcsmArgval argval, UpcsmMeters meters, UpperExesvc exesvc)
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

    private UpcsmWorker(UpcsmArgval argval, UpcsmMeters meters, UpperExesvc exesvc)
    {
        super(argval, meters, exesvc);
    }

    @Override
    public UpcsmActionRun action()
    {
        return UpcsmActionRun.of(this.argval.actionInfos.get(0));
    }
}
