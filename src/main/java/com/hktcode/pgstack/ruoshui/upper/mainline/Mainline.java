/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.ZonedDateTime;

public class Mainline implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(Mainline.class);

    public static Mainline of(MainlineConfig config, MainlineSender sender)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        return new Mainline(config, sender);
    }

    public final MainlineConfig config;

    public final MainlineSender sender;

    private Mainline(MainlineConfig config, MainlineSender sender)
    {
        this.config = config;
        this.sender = sender;
    }

    @Override
    public void run()
    {
        try {
            this.runWithInterrupted();
        }
        catch (InterruptedException ex) {
            logger.error("should not be interrupted by other thread.");
            Thread.currentThread().interrupt();
        }
    }

    private void runWithInterrupted() throws InterruptedException
    {
        ZonedDateTime startMillis = ZonedDateTime.now();
        MainlineStep action = MainlineStepStarts.of(config, sender, startMillis);
        try {
            action = ((MainlineStepStarts)action).next();
            if (!(action instanceof MainlineStepNormal)) {
                return;
            }
            try (Connection repl = config.srcProperty.replicaConnection()) {
                PgConnection pgrepl = repl.unwrap(PgConnection.class);
                do {
                    action = ((MainlineStepNormal) action).next(pgrepl);
                } while (action instanceof MainlineStepNormal);
            }
        }
        catch (Exception ex) {
            logger.error("throws exception: ", ex);
            ZonedDateTime endtime = ZonedDateTime.now();
            MainlineMetric metric = action.getMetric();
            metric.statusInfor = "throw exception: " + ex.getMessage();
            long timeout = config.logDuration;
            MainlineRecord r = MainlineRecordThrows.of(endtime, ex);
            sender.send(r, timeout, timeout, metric);
        }
    }
}
