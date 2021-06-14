package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorkerGauges;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorkerGauges;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorkerGauges;

import java.util.concurrent.atomic.AtomicLong;

public class UpperHolderGauges
{
    public static UpperHolderGauges of()
    {
        return new UpperHolderGauges();
    }

    public final long createts;

    public final UpcsmWorkerGauges consumer;

    public final UpjctWorkerGauges junction;

    public final UppdcWorkerGauges producer;

    private UpperHolderGauges()
    {
        this.createts = System.currentTimeMillis();
        AtomicLong txactionLsn = new AtomicLong(0L);
        this.consumer = UpcsmWorkerGauges.of(txactionLsn);
        this.junction = UpjctWorkerGauges.of();
        this.producer = UppdcWorkerGauges.of(txactionLsn);
    }
}
