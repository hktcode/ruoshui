/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UppdcWkstepActionKafka extends UppdcWkstepAction
{
    public static UppdcWkstepActionKafka of(UppdcWkstepArgvalKafka config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new UppdcWkstepActionKafka(config);
    }

    private final UppdcWkstepArgvalKafka config;

    private UppdcWkstepActionKafka(UppdcWkstepArgvalKafka config)
    {
        this.config = config;
    }

    @Override
    protected UppdcSenderKafka sender()
    {
        return UppdcSenderKafka.of(config, UppdcWkstepMetricKafka.of());
    }
}
