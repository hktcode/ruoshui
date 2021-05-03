/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UppdcActionRunKafka extends UppdcActionRun
{
    public static UppdcActionRunKafka of(UppdcConfigKafka config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new UppdcActionRunKafka(config);
    }

    private final UppdcConfigKafka config;

    private UppdcActionRunKafka(UppdcConfigKafka config)
    {
        this.config = config;
    }

    @Override
    protected UppdcSenderKafka sender()
    {
        return UppdcSenderKafka.of(config, UppdcMetricKafka.of());
    }
}
