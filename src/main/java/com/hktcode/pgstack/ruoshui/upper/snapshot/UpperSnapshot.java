/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshot;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class UpperSnapshot<T>
{
    private static final Logger logger = LoggerFactory.getLogger(UpperSnapshot.class);

    protected final PgSnapshotConfig config;

    private final UpperSnapshotSender<T> sender;

    protected UpperSnapshot
        /* */( PgSnapshotConfig config //
        /* */, UpperSnapshotSender<T> sender //
        /* */)
    {
        this.config = config;
        this.sender = sender;
    }

    public void run()
    {
        try {
            this.runInternal();
        }
        catch (Exception ex) {
            logger.error("snapshot throws exception.", ex); // TODO:
        }
    }

    protected abstract void runInternal() throws SQLException, InterruptedException;

    protected void run(PgConnection pgrepl) throws SQLException, InterruptedException
    {
        try (Connection data = config.srcProperty.queriesConnection()) {
            PgConnection pgdata = data.unwrap(PgConnection.class);
            PgSnapshot<UpperSnapshotMetric> runnable //
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
    }
}
