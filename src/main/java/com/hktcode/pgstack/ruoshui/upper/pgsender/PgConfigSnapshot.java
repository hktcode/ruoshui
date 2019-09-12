/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;

import java.sql.Statement;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PgConfigSnapshot extends PgConfig
{
    public static PgConfigSnapshot of //
        /* */( PgConnectionProperty srcProperty //
        /* */, String relationSql //
        /* */, PgFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */, String typelistSql //
        /* */) //
    {
        if (srcProperty == null) {
            throw new ArgumentNullException("srcProperty");
        }
        if (relationSql == null) {
            throw new ArgumentNullException("relationSql");
        }
        if (whereScript == null) {
            throw new ArgumentNullException("whereScript");
        }
        if (lockingMode == null) {
            throw new ArgumentNullException("lockingMode");
        }
        if (logicalRepl == null) {
            throw new ArgumentNullException("logicalRepl");
        }
        if (tupleSelect == null) {
            throw new ArgumentNullException("tupleSelect");
        }
        return new PgConfigSnapshot //
            /* */( srcProperty //
            /* */, relationSql //
            /* */, whereScript //
            /* */, lockingMode //
            /* */, logicalRepl //
            /* */, tupleSelect //
            /* */, typelistSql //
            /* */);
    }

    private PgConfigSnapshot //
        /* */( PgConnectionProperty srcProperty //
        /* */, String relationSql //
        /* */, PgFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */, String typelistSql //
        /* */) //
    {
        super(srcProperty, relationSql, whereScript, lockingMode, logicalRepl, tupleSelect, typelistSql);
    }

    @Override
    public PgDeputeCreateSlotSnapshot newCreateSlot(Statement statement)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        return PgDeputeCreateSlotSnapshot.of(statement, logicalRepl.slotName);
    }

    @Override
    public PgAction createsAction(AtomicReference<SimpleStatus> status, TransferQueue<PgRecord> tqueue)
    {
        return PgActionDataRelaList.of(this, status, tqueue);
    }

    @Override
    public PgAction afterSnapshot(PgActionDataSsFinish action)
    {
        return PgActionDataSsReturn.of(action);
    }
}
