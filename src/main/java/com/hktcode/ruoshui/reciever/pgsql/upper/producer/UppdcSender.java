package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;

public interface UppdcSender extends AutoCloseable
{
    void send(UppdcWorkerGauges gauges, UpperRecordProducer record) throws Exception;
}
