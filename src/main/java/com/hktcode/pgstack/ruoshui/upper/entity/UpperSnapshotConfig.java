/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalStreamStarter;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;

public class UpperSnapshotConfig extends UpperConsumerConfig
{
    public static UpperSnapshotConfig of //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalStreamStarter logicalSlot //
        /* */, PgSnapshotConfig iniSnapshot //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */)
    {
        if (srcProperty == null) {
            throw new ArgumentNullException("srcProperty");
        }
        if (logicalSlot == null) {
            throw new ArgumentNullException("logicalRepl");
        }
        if (iniSnapshot == null) {
            throw new ArgumentNullException("iniSnapshot");
        }
        return new UpperSnapshotConfig //
            /* */( srcProperty //
            /* */, logicalSlot //
            /* */, iniSnapshot //
            /* */, waitTimeout //
            /* */, logDuration //
            /* */);
    }

    public final PgSnapshotConfig iniSnapshot;

    private UpperSnapshotConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalStreamStarter logicalSlot //
        /* */, PgSnapshotConfig iniSnapshot //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */)
    {
        super(srcProperty, logicalSlot, waitTimeout, logDuration);
        this.iniSnapshot = iniSnapshot;
    }
}
