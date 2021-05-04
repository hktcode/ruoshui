/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UppdcWkstepActionFiles extends UppdcWkstepAction
{
    public static UppdcWkstepActionFiles of(UppdcWkstepArgvalFiles config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new UppdcWkstepActionFiles(config);
    }

    public final UppdcWkstepArgvalFiles config;

    private UppdcWkstepActionFiles(UppdcWkstepArgvalFiles config)
    {
        this.config = config;
    }

    @Override
    protected UppdcSenderFiles sender() throws IOException
    {
        Path directory = Paths.get(config.walDatapath.toString());
        Files.createDirectories(directory);
        return UppdcSenderFiles.of(config, UppdcWkstepMetricFiles.of());
    }
}
