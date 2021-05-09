package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorkerMeters;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorkerMeters;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorkerMeters;

import java.util.concurrent.atomic.AtomicLong;

public class UpperExesvcGauges
{
    public static UpperExesvcGauges of()
    {
        return new UpperExesvcGauges();
    }

    public final long createts;

    public final UpcsmWorkerMeters consumer;

    public final UpjctWorkerMeters junction;

    public final UppdcWorkerMeters producer;

    private UpperExesvcGauges()
    {
        this.createts = System.currentTimeMillis();
        AtomicLong txactionLsn = new AtomicLong(0L);
        this.consumer = UpcsmWorkerMeters.of(txactionLsn);
        this.junction = UpjctWorkerMeters.of();
        this.producer = UppdcWorkerMeters.of(txactionLsn);
    }
}
