package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWorkerGauges;

public class UpjctWorkerGauges extends SimpleWorkerGauges
{
    public static UpjctWorkerGauges of(UpjctWorkerArgval argval) // Xqueue.Fetch<UpperRecordConsumer> fetchMetric, Xqueue.Offer<UpperRecordProducer> offerMetric)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        return new UpjctWorkerGauges(argval);
    }

    public final Xqueue.Fetch<UpperRecordConsumer> fetchMetric;
    public final Xqueue.Offer<UpperRecordProducer> offerMetric;
    public final Xqueue.Spins spinsMetric;

    private UpjctWorkerGauges(UpjctWorkerArgval argval)
    {
        this.fetchMetric = argval.fetchXqueue.fetchXqueue();
        this.offerMetric = argval.offerXqueue.offerXqueue();
        this.spinsMetric = argval.spinsArgval;
    }
}
