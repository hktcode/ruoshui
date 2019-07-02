/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;

public class MainlineConfigSnapshot extends MainlineConfig
{
    public static MainlineConfigSnapshot of //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalReplConfig logicalRepl //
        /* */, PgSnapshotConfig iniSnapshot //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */)
    {
        if (srcProperty == null) {
            throw new ArgumentNullException("srcProperty");
        }
        if (logicalRepl == null) {
            throw new ArgumentNullException("logicalRepl");
        }
        if (iniSnapshot == null) {
            throw new ArgumentNullException("iniSnapshot");
        }
        return new MainlineConfigSnapshot //
            /* */( srcProperty //
            /* */, logicalRepl //
            /* */, iniSnapshot //
            /* */, waitTimeout //
            /* */, logDuration //
            /* */);
    }

    public final PgSnapshotConfig iniSnapshot;

    private MainlineConfigSnapshot //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalReplConfig logicalRepl //
        /* */, PgSnapshotConfig iniSnapshot //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */)
    {
        super(srcProperty, logicalRepl, waitTimeout, logDuration);
        this.iniSnapshot = iniSnapshot;
    }
}
