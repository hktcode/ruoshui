package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleHolder;
import com.hktcode.simple.SimpleWorker;

public class UppdcWorker extends SimpleWorker<UppdcWorkerArgval, UppdcWorkerMeters>
{
    public static UppdcWorker of(UppdcWorkerArgval argval, UppdcWorkerMeters meters, SimpleHolder holder, Tqueue<UpperRecordProducer> target)
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
        if (target == null) {
            throw new ArgumentNullException("target");
        }
        return new UppdcWorker(argval, meters, holder, target);
    }

    private final Tqueue<UpperRecordProducer> target;

    private UppdcWorker(UppdcWorkerArgval argval, UppdcWorkerMeters meters, SimpleHolder holder, Tqueue<UpperRecordProducer> target)
    {
        super(argval, meters, holder);
        this.target = target;
    }

    @Override
    public UppdcWkstepAction action()
    {
        return this.argval.actionInfos.get(0).action(this.target);
    }
}
