/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;

public class MainlineConfigTxaction extends MainlineConfig
{
    public static MainlineConfigTxaction of //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalReplConfig logicalRepl //
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
        return new MainlineConfigTxaction(srcProperty, logicalRepl, waitTimeout, logDuration);
    }

    private MainlineConfigTxaction //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalReplConfig logicalSlot //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */)
    {
        super(srcProperty, logicalSlot, waitTimeout, logDuration);
    }
}
