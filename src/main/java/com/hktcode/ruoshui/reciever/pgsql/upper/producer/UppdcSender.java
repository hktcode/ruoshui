package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;

public abstract class UppdcSender implements AutoCloseable
{
    protected UppdcSender()
    {
    }

    public abstract void send(UppdcWorkerMeters meters, UpperRecordProducer record) throws Exception;
}
