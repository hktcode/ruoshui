package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalTxactContext;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWorkerGauges;

public class UpjctWorkerGauges extends SimpleWorkerGauges
{
    public static UpjctWorkerGauges of(UpjctWorkerArgval argval)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        return new UpjctWorkerGauges(argval);
    }

    public final Xqueue.Fetch<UpperRecordConsumer> recver;
    public final Xqueue.Offer<UpperRecordProducer> sender;
    public final Xqueue.Spins xspins;

    public long curlsn = 0;
    public long curseq = 0;
    public final LogicalTxactContext xidenv = LogicalTxactContext.of();

    private UpjctWorkerGauges(UpjctWorkerArgval argval)
    {
        this.recver = argval.recver.fetchXqueue();
        this.sender = argval.sender.offerXqueue();
        this.xspins = argval.xspins;
    }
}
