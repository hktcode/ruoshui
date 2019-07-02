/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshot;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class Snapshot implements Runnable
{
    public static Snapshot of(PgSnapshotConfig config, SnapshotSender sender)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        return new Snapshot(config, sender);
    }

    private static final Logger logger = LoggerFactory.getLogger(Snapshot.class);

    private final PgSnapshotConfig config;

    private final SnapshotSender sender;

    private Snapshot(PgSnapshotConfig config, SnapshotSender sender)
    {
        this.config = config;
        this.sender = sender;
    }

    public void run()
    {
        try (Connection repl = this.config.srcProperty.replicaConnection();
             Connection data = this.config.srcProperty.queriesConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            PgConnection pgdata = data.unwrap(PgConnection.class);
            PgSnapshot<SnapshotMetric> runnable //
                = PgSnapshot.of(config, pgrepl, pgdata, sender);
            Thread t = new Thread(runnable);
            t.start();
            while (!sender.isDone()) {
                if (!t.isAlive()) {
                    return;
                }
                t.join(this.config.waitTimeout);
            }
            t.interrupt();
            pgdata.cancelQuery();
            pgrepl.cancelQuery();
        }
        catch (Exception ex) {
            logger.error("snapshot throws exception.", ex); // TODO:
        }
    }
}
