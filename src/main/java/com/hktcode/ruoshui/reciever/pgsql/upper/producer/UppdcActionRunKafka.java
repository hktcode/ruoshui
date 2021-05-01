/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;

class UppdcActionRunKafka extends UppdcActionRun<UppdcConfigKafka, UppdcMetricKafka>
{
    public static UppdcActionRunKafka //
    of(UppdcConfigKafka config, UppdcMetricKafka metric, UpperHolder holder)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new UppdcActionRunKafka(config, metric, holder);
    }

    private UppdcActionRunKafka(UppdcConfigKafka config, UppdcMetricKafka metric, UpperHolder holder)
    {
        super(config, metric, holder);
    }

    @Override
    protected UppdcSenderKafka sender()
    {
        return UppdcSenderKafka.of(this.config, this.metric);
    }
}
