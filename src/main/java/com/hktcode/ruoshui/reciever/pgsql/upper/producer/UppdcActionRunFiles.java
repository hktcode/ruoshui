/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UppdcActionRunFiles extends UppdcActionRun<UppdcConfigFiles, UppdcMetricFiles>
{
    public static UppdcActionRunFiles //
    of(UppdcConfigFiles config, UppdcMetricFiles metric, UpperExesvc exesvc)
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
        return new UppdcActionRunFiles(config, metric, exesvc);
    }

    private UppdcActionRunFiles(UppdcConfigFiles config, UppdcMetricFiles metric, UpperExesvc exesvc)
    {
        super(config, metric, exesvc);
    }

    @Override
    protected UppdcSenderFiles sender() throws IOException
    {
        Path directory = Paths.get(config.walDatapath.toString());
        Files.createDirectories(directory);
        return UppdcSenderFiles.of(this.config, this.metric);
    }
}
