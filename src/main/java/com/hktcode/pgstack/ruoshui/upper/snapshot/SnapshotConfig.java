/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableMap;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.upper.mainline.PgLockMode;

public class SnapshotConfig extends PgSnapshotConfig
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
}
