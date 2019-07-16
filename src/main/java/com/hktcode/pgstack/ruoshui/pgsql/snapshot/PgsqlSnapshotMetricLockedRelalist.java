/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;

import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 对指定的关系加锁.
 */
public interface PgsqlSnapshotMetricLockedRelalist extends PgsqlSnapshotMetricProcess
{
    // public static PgsqlSnapshotMetricLockedRelalist of//
    //     /* */( PgsqlSnapshotMetricSelectRelalist metric
    //     /* */, long oldDuration
    //     /* */, ImmutableList<PgsqlRelationMetric> relationLst
    //     /* */) //
    // {
    //     if (metric == null) {
    //         throw new ArgumentNullException("metric");
    //     }
    //     return new PgsqlSnapshotMetricLockedRelalist(metric, oldDuration, relationLst);
    // }

    // private PgsqlSnapshotMetricLockedRelalist //
    //     /* */( PgsqlSnapshotMetricSelectRelalist metric
    //     /* */, long oldDuration //
    //     /* */, ImmutableList<PgsqlRelationMetric> relationLst
    //     /* */) //
    // {
    //     super(metric, oldDuration, relationLst);
    // }

    static <W extends SimpleWorker<W, PgsqlSnapshotMetric> & PgsqlSnapshot<W>>
    PgsqlSnapshotMetric next
        /* */( ExecutorService exesvc
        /* */, W worker
        /* */, PgConnection pgdata
        /* */, PgConnection pgrepl
        /* */, PgsqlSnapshotMetricLockedRelalist metric
        /* */, List<PgsqlRelationMetric> relalist
        /* */)
        throws Exception
    {
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        // TODO: 研究一下PostgreSQL的锁机制，看看能不能加上更低级的锁.
        try (Statement statement = pgdata.createStatement()) {
            Iterator<PgsqlRelationMetric> iterator = relalist.iterator();
            PgsqlRelationMetric r = null;
            Future<Boolean> future = null;
            Boolean success = null;
            while (worker.newStatus(worker, metric) instanceof SimpleStatusInnerRun) {
                if (Boolean.FALSE.equals(success)) {
                    return metric.newAbortsLockFail(r);
                }
                else if (Boolean.TRUE.equals(success)) {
                    future = null;
                    r = null;
                    success = null;
                }
                else if (future != null) {
                    success = worker.pollFromFuture(future);
                }
                else if (iterator.hasNext()) {
                    r = iterator.next();
                    PgReplRelation relation = r.relationInfo;
                    future = exesvc.submit(()->worker.lockedRelation(statement, relation));
                }
                else {
                    return metric.newCreateReplSlot();
                }
            }
        }
        pgdata.cancelQuery();
        return metric.newCommit();
    }

    PgsqlSnapshotMetricCreateReplSlot newCreateReplSlot();
    PgsqlSnapshotMetricAbortsLockFail newAbortsLockFail(PgsqlRelationMetric relation);
    PgsqlSnapshotMetricCommit newCommit();
}
