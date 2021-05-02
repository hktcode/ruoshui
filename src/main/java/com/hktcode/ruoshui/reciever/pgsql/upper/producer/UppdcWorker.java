package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleWorker;

public class UppdcWorker extends SimpleWorker<UppdcConfig, UppdcMetric, UpperExesvc>
{
    public static UppdcWorker of(UppdcConfig config, UppdcMetric metric, UpperExesvc exesvc)
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
        return new UppdcWorker(config, metric, exesvc);
    }

    private UppdcWorker(UppdcConfig config, UppdcMetric metric, UpperExesvc exesvc)
    {
        super(config, metric, exesvc);
    }

    public UppdcActionRun action()
    {
        if (this.config instanceof UppdcConfigKafka)  {
            return UppdcActionRunKafka.of((UppdcConfigKafka)config, (UppdcMetricKafka)metric, exesvc);
        } else {
            return UppdcActionRunFiles.of((UppdcConfigFiles)config, (UppdcMetricFiles)metric, exesvc);
        }
    }
}
