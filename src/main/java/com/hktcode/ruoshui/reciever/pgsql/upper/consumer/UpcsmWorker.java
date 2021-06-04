package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.SimpleHolder;
import com.hktcode.simple.SimpleWorker;

public class UpcsmWorker extends SimpleWorker<UpcsmWorkerArgval, UpcsmWorkerMeters>
{
    public static UpcsmWorker of(UpcsmWorkerArgval argval, UpcsmWorkerMeters meters, SimpleHolder holder, Tqueue<UpperRecordConsumer> source)
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
        if (source == null) {
            throw new ArgumentNullException("source");
        }
        return new UpcsmWorker(argval, meters, holder, source);
    }

    private final Tqueue<UpperRecordConsumer> source;

    private UpcsmWorker(UpcsmWorkerArgval argval, UpcsmWorkerMeters meters, SimpleHolder holder, Tqueue<UpperRecordConsumer> source)
    {
        super(argval, meters, holder);
        this.source = source;
    }

    @Override
    public UpcsmWkstepAction action()
    {
        return this.argval.actionInfos.get(0).action(this.source);
    }
}
