package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorkerArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorkerArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorkerGauges;

public class UpperHolderGauges
{
    public static UpperHolderGauges of(UpperHolderArgval argval)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        return new UpperHolderGauges(argval);
    }

    public final long createts;

    public final UpcsmWorkerArgval consumer;

    public final UpjctWorkerArgval junction;

    public final UppdcWorkerGauges producer;

    private UpperHolderGauges(UpperHolderArgval argval)
    {
        this.createts = System.currentTimeMillis();
        this.consumer = argval.consumer;
        this.junction = argval.junction;
        this.producer = UppdcWorkerGauges.of(argval.producer);
    }
}
