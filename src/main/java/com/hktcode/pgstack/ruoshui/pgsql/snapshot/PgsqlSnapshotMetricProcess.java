/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.bgsimple.SimpleWorker;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.ExecutorService;

public interface PgsqlSnapshotMetricProcess extends PgsqlSnapshotMetric
{
    // public final ZonedDateTime startMillis;

    // public final long retryCounts;

    // public long recordCount = 0;

    // public long fetchCounts = 0;

    // public long fetchMillis = 0;

    // public long offerCounts = 0;

    // public long offerMillis = 0;

    // public long logDatetime = 0;

    // public String statusInfor = "";

    // public final long oldDuration;

    // public final ImmutableList<PgsqlRelationMetric> relationLst;

    // protected PgsqlSnapshotMetricProcess
    //     /* */( PgsqlSnapshotMetricSelectRelalist metric
    //     /* */, long oldDuration
    //     /* */, ImmutableList<PgsqlRelationMetric> relationLst
    //     /* */)
    // {
    //     this.oldDuration = oldDuration;
    //     this.relationLst = relationLst;
    //     this.startMillis = metric.startMillis;
    //     this.retryCounts = metric.retryCounts;
    // }

    // protected PgsqlSnapshotMetricProcess(PgsqlSnapshotMetricProcess metric)
    // {
    //     this.oldDuration = metric.oldDuration;
    //     this.relationLst = metric.relationLst;
    //     this.startMillis = metric.startMillis;
    //     this.retryCounts = metric.retryCounts;
    //     this.recordCount = metric.recordCount;
    //     this.fetchCounts = metric.fetchCounts;
    //     this.fetchMillis = metric.fetchMillis;
    //     this.offerCounts = metric.offerCounts;
    //     this.offerMillis = metric.offerMillis;
    //     this.logDatetime = metric.logDatetime;
    //     this.statusInfor = metric.statusInfor;
    // }

    <W extends PgsqlSnapshot<W>>
    PgsqlSnapshotMetric next //
        /* */( ExecutorService exesvc //
        /* */, W worker //
        /* */, PgConnection pgdata //
        /* */, PgConnection pgrepl //
        /* */) //
        throws Exception;
}
