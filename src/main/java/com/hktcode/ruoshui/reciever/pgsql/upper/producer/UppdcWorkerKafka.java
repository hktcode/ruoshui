package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;

public class UppdcWorkerKafka extends UppdcWorker<UppdcConfigKafka, UppdcMetricKafka>
{
    public static UppdcWorkerKafka of(UppdcConfigKafka config, UppdcMetricKafka metric, UpperExesvc exesvc)
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
        return new UppdcWorkerKafka(config, metric, exesvc);
    }

    private UppdcWorkerKafka(UppdcConfigKafka config, UppdcMetricKafka metric, UpperExesvc exesvc)
    {
        super(config, metric, exesvc);
    }

    @Override
    public UppdcActionRunKafka action()
    {
        return UppdcActionRunKafka.of();
    }
}
