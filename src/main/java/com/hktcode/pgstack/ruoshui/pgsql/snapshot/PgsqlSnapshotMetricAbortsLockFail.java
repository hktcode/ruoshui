/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.pgjdbc.PgReplRelation;

public interface PgsqlSnapshotMetricAbortsLockFail extends PgsqlSnapshotMetricAborts
{
    // public static PgsqlSnapshotMetricAbortsLockFail of
    //     /* */( PgsqlSnapshotMetricLockedRelalist metric
    //     /* */, PgReplRelation lockfailRel
    //     /* */)
    // {
    //     return new PgsqlSnapshotMetricAbortsLockFail(metric, lockfailRel);
    // }

    // private final PgReplRelation lockfailRel;

    // /**
    //  * constructor.
    //  *
    //  * @param metric
    //  */
    // private PgsqlSnapshotMetricAbortsLockFail
    //     /* */( PgsqlSnapshotMetricLockedRelalist metric
    //     /* */, PgReplRelation lockfailRel
    //     /* */)
    // {
    //     super(metric);
    //     this.lockfailRel = lockfailRel;
    // }
}
