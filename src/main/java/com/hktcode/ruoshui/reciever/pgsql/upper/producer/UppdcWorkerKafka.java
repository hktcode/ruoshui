package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;

public class UppdcWorkerKafka extends UppdcWorker
{
    public static UppdcWorkerKafka of(UppdcConfigKafka config, UppdcMeters meters, UpperExesvc exesvc)
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
        return new UppdcWorkerKafka(config, meters, exesvc);
    }

    private UppdcWorkerKafka(UppdcConfigKafka config, UppdcMeters meters, UpperExesvc exesvc)
    {
        super(config, meters, exesvc);
    }

    @Override
    public UppdcActionRunKafka action()
    {
        return UppdcActionRunKafka.of((UppdcConfigKafka) config);
    }
}
