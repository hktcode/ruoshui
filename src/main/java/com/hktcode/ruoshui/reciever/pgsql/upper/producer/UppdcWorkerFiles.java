package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;

public class UppdcWorkerFiles extends UppdcWorker<UppdcConfigFiles, UppdcMetricFiles>
{
    public static UppdcWorkerFiles of(UppdcConfigFiles config, UppdcMetricFiles metric, UpperExesvc exesvc)
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
        return new UppdcWorkerFiles(config, metric, exesvc);
    }

    private UppdcWorkerFiles(UppdcConfigFiles config, UppdcMetricFiles metric, UpperExesvc exesvc)
    {
        super(config, metric, exesvc);
    }

    @Override
    public UppdcActionRunFiles action()
    {
        return UppdcActionRunFiles.of();
    }
}
