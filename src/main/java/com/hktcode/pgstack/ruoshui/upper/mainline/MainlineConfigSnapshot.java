package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalStreamStarter;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;

public class MainlineConfigSnapshot extends MainlineConfig
{
    public static MainlineConfigSnapshot of //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalStreamStarter logicalRepl //
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
        /* */, LogicalStreamStarter logicalRepl //
        /* */, PgSnapshotConfig iniSnapshot //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */)
    {
        super(srcProperty, logicalRepl, waitTimeout, logDuration);
        this.iniSnapshot = iniSnapshot;
    }
}
