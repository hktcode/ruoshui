package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;

public class UpperQueues
{
    public static UpperQueues of(Tqueue<UpperRecordConsumer> source, Tqueue<UpperRecordProducer> target)
    {
        if (source == null) {
            throw new ArgumentNullException("source");
        }
        if (target == null) {
            throw new ArgumentNullException("target");
        }
        return new UpperQueues(source, target);
    }

    public final Tqueue<UpperRecordConsumer> source;

    public final Tqueue<UpperRecordProducer> target;

    private UpperQueues(Tqueue<UpperRecordConsumer> source, Tqueue<UpperRecordProducer> target)
    {
        this.source = source;
        this.target = target;
    }
}
