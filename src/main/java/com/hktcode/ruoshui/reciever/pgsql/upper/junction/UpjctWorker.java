package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperQueues;
import com.hktcode.simple.SimpleHolder;
import com.hktcode.simple.SimpleWorker;

public class UpjctWorker extends SimpleWorker<UpjctWorkerArgval, UpjctWorkerMeters>
{
    public static UpjctWorker of(UpjctWorkerArgval config, UpjctWorkerMeters meters, SimpleHolder holder, UpperQueues queues)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        if (queues == null) {
            throw new ArgumentNullException("queues");
        }
        return new UpjctWorker(config, meters, holder, queues);
    }

    private final UpperQueues queues;

    private UpjctWorker(UpjctWorkerArgval config, UpjctWorkerMeters meters, SimpleHolder holder, UpperQueues queues)
    {
        super(config, meters, holder);
        this.queues = queues;
    }

    @Override
    public UpjctWkstepAction action()
    {
        return this.argval.actionInfos.get(0).action(this.queues);
    }
}
