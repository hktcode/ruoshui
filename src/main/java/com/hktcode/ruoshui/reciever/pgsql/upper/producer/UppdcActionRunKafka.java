/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

public class UppdcActionRunKafka extends UppdcActionRun<UppdcConfigKafka, UppdcMetricKafka>
{
    public static UppdcActionRunKafka of()
    {
        return new UppdcActionRunKafka();
    }

    private UppdcActionRunKafka()
    {
        super();
    }

    @Override
    protected UppdcSenderKafka sender(UppdcConfigKafka config, UppdcMetricKafka metric)
    {
        return UppdcSenderKafka.of(config, metric);
    }
}
