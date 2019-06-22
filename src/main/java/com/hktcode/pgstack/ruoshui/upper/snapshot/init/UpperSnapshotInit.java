/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.init;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.upper.snapshot.UpperSnapshot;
import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;

public class UpperSnapshotInit extends UpperSnapshot<UpperSnapshotInitRecord> implements Runnable
{
    public static UpperSnapshotInit of //
        /* */(PgSnapshotConfig config //
        /* */, UpperSnapshotInitSender sender //
        /* */, PgConnection pgrepl //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        return new UpperSnapshotInit(config, sender, pgrepl);
    }

    private final PgConnection pgrepl;

    private UpperSnapshotInit //
        /* */( PgSnapshotConfig config //
        /* */, UpperSnapshotInitSender sender //
        /* */, PgConnection pgrepl //
        /* */)
    {
        super(config, sender);
        this.pgrepl = pgrepl;
    }

    @Override
    protected void runInternal() throws SQLException, InterruptedException
    {
        super.run(pgrepl);
    }
}
