package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;

public abstract class UppdcSender<C extends UppdcConfig, M extends UppdcMetric> implements AutoCloseable
{
    public final C config;

    public final M metric;

    protected UppdcSender(C config, M metric)
    {
        this.config = config;
        this.metric = metric;
    }

    public abstract void send(UpperRecordProducer record) throws Exception;
}
