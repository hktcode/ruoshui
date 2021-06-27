package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorkerGauges;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorkerGauges;
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

    public final UpcsmWorkerGauges consumer;

    public final UpjctWorkerGauges junction;

    public final UppdcWorkerGauges producer;

    private UpperHolderGauges(UpperHolderArgval argval)
    {
        this.createts = System.currentTimeMillis();
        this.consumer = UpcsmWorkerGauges.of(argval.consumer);
        this.junction = UpjctWorkerGauges.of(argval.junction);
        this.producer = UppdcWorkerGauges.of(argval.producer);
    }
}
