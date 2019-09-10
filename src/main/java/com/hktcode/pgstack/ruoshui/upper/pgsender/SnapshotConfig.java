/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableMap;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;

import java.sql.Statement;
import java.util.concurrent.Callable;

public class SnapshotConfig extends PgsenderConfig
{
    public static SnapshotConfig of //
        /* */( PgConnectionProperty srcProperty //
        /* */, String relationSql //
        /* */, PgSnapshotFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
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
        return new SnapshotConfig //
            /* */( srcProperty //
            /* */, relationSql //
            /* */, whereScript //
            /* */, lockingMode //
            /* */, logicalRepl //
            /* */, tupleSelect //
            /* */);
    }

    private SnapshotConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, String relationSql //
        /* */, PgSnapshotFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */) //
    {
        super(srcProperty, relationSql, whereScript, lockingMode, logicalRepl, tupleSelect);
    }

    @Override
    public Callable<PgReplSlotTuple> newCreateSlot(Statement statement)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        return DeputeCreateReplSlotSnapshot.of(statement, logicalRepl.slotName);
    }

    @Override
    public PgRecordPauseWorld pauseWorldMsg()
    {
        return PgRecordPauseWorld.of();
    }

    @Override
    public PgRecordCreateSlot createSlotMsg(PgReplSlotTuple tuple)
    {
        if (tuple == null) {
            throw new ArgumentNullException("tuple");
        }
        return PgRecordCreateSlot.of(tuple);
    }
}
