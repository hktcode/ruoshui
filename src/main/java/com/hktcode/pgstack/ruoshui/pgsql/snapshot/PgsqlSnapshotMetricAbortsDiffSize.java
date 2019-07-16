/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.pgjdbc.PgReplRelation;

public interface PgsqlSnapshotMetricAbortsDiffSize extends PgsqlSnapshotMetricAborts
{
    // public static PgsqlSnapshotMetricAbortsDiffSize of
    //     /* */( PgsqlSnapshotMetricVerifyRelalist metric
    //     /* */, ImmutableList<PgReplRelation> oldRelalist
    //     /* */, ImmutableList<PgReplRelation> newRelalist
    //     /* */)
    // {
    //     return new PgsqlSnapshotMetricAbortsDiffSize(metric, oldRelalist, newRelalist);
    // }

    // public final ImmutableList<PgReplRelation> oldRelalist;

    // public final ImmutableList<PgReplRelation> newRelalist;

    // protected PgsqlSnapshotMetricAbortsDiffSize
    //     /* */( PgsqlSnapshotMetricVerifyRelalist metric
    //     /* */, ImmutableList<PgReplRelation> oldRelalist
    //     /* */, ImmutableList<PgReplRelation> newRelalist
    //     /* */)
    // {
    //     super(metric);
    //     this.oldRelalist = oldRelalist;
    //     this.newRelalist = newRelalist;
    // }
}
