package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;

public abstract class UppdcSender implements AutoCloseable
{
    protected UppdcSender()
    {
    }

    public abstract void send(UppdcWorkerGauges meters, UpperRecordProducer record) throws Exception;
}
