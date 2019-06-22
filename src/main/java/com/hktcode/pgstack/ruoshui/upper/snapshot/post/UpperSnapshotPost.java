/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.post;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.upper.snapshot.UpperSnapshot;
import org.postgresql.jdbc.PgConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class UpperSnapshotPost extends UpperSnapshot<UpperSnapshotPostRecord> implements Runnable
{
    public static UpperSnapshotPost of //
        /* */( PgSnapshotConfig config //
        /* */, UpperSnapshotPostSender sender //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        return new UpperSnapshotPost(config, sender);
    }

    private UpperSnapshotPost //
        /* */( PgSnapshotConfig config //
        /* */, UpperSnapshotPostSender sender
        /* */)
    {
        super(config, sender);
    }

    @Override
    protected void runInternal() throws SQLException, InterruptedException
    {
        try (Connection repl = this.config.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            super.run(pgrepl);
        }
    }
}
