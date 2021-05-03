package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;

public class UppdcWorkerFiles extends UppdcWorker
{
    public static UppdcWorkerFiles of(UppdcConfigFiles config, UppdcMeters meters, UpperExesvc exesvc)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        return new UppdcWorkerFiles(config, meters, exesvc);
    }

    private UppdcWorkerFiles(UppdcConfigFiles config, UppdcMeters meters, UpperExesvc exesvc)
    {
        super(config, meters, exesvc);
    }

    @Override
    public UppdcActionRunFiles action()
    {
        return UppdcActionRunFiles.of((UppdcConfigFiles) config);
    }
}
