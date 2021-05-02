/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;

public class UppdcActionRunKafka extends UppdcActionRun<UppdcConfigKafka, UppdcMetricKafka>
{
    public static UppdcActionRunKafka //
    of(UppdcConfigKafka config, UppdcMetricKafka metric, UpperExesvc exesvc)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        return new UppdcActionRunKafka(config, metric, exesvc);
    }

    private UppdcActionRunKafka(UppdcConfigKafka config, UppdcMetricKafka metric, UpperExesvc exesvc)
    {
        super(config, metric, exesvc);
    }

    @Override
    protected UppdcSenderKafka sender()
    {
        return UppdcSenderKafka.of(this.config, this.metric);
    }
}
