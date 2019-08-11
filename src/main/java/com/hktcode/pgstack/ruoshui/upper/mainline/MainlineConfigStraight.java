/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineConfigStraight extends MainlineConfig
{
    public static MainlineConfigStraight of //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalReplConfig logicalRepl //
        /* */, String typelistSql //
        /* */)
    {
        if (srcProperty == null) {
            throw new ArgumentNullException("srcProperty");
        }
        if (logicalRepl == null) {
            throw new ArgumentNullException("logicalRepl");
        }
        return new MainlineConfigStraight(srcProperty, logicalRepl, typelistSql);
    }

    private MainlineConfigStraight //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalReplConfig logicalSlot //
        /* */, String typelistSql //
        /* */)
    {
        super(srcProperty, logicalSlot, typelistSql);
    }

    @Override
    public MainlineActionDataBegin1stStraight createsAction //
        /* */( AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */) //
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        return MainlineActionDataBegin1stStraight.of(this, status, tqueue);
    }
}
