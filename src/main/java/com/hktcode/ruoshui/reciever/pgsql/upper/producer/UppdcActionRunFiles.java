/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UppdcActionRunFiles extends UppdcActionRun
{
    public static UppdcActionRunFiles of(UppdcConfigFiles config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new UppdcActionRunFiles(config);
    }

    public final UppdcConfigFiles config;

    private UppdcActionRunFiles(UppdcConfigFiles config)
    {
        this.config = config;
    }

    @Override
    protected UppdcSenderFiles sender() throws IOException
    {
        Path directory = Paths.get(config.walDatapath.toString());
        Files.createDirectories(directory);
        return UppdcSenderFiles.of(config, UppdcMetricFiles.of());
    }
}
