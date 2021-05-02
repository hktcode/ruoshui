/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UppdcActionRunFiles extends UppdcActionRun<UppdcConfigFiles, UppdcMetricFiles>
{
    public static UppdcActionRunFiles of()
    {
        return new UppdcActionRunFiles();
    }

    private UppdcActionRunFiles()
    {
    }

    @Override
    protected UppdcSenderFiles sender(UppdcConfigFiles config, UppdcMetricFiles metric) //
            throws IOException
    {
        Path directory = Paths.get(config.walDatapath.toString());
        Files.createDirectories(directory);
        return UppdcSenderFiles.of(config, metric);
    }
}
