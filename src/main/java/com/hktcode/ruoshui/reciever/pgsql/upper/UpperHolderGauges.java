package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorkerGauges;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorkerGauges;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorkerGauges;

import java.util.concurrent.atomic.AtomicLong;

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
        AtomicLong txactionLsn = new AtomicLong(0L);
        this.consumer = UpcsmWorkerGauges.of(argval.consumer, txactionLsn);
        this.junction = UpjctWorkerGauges.of(argval.junction);
        this.producer = UppdcWorkerGauges.of(argval.producer, txactionLsn);
    }
}
