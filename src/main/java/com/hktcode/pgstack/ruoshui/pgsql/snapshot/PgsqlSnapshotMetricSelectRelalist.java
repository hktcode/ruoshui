/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface PgsqlSnapshotMetricSelectRelalist extends PgsqlSnapshotMetric
{
    // public static PgsqlSnapshotMetricSelectRelalist of
    //     /* */( ZonedDateTime startMillis //
    //     /* */, long retryCounts //
    //     /* */) //
    // {
    //     if (startMillis == null) {
    //         throw new ArgumentNullException("startMillis");
    //     }
    //     return new PgsqlSnapshotMetricSelectRelalist(startMillis, retryCounts);
    // }

    // public final ZonedDateTime startMillis;

    // public final long retryCounts;

    // private PgsqlSnapshotMetricSelectRelalist
    //    /* */( ZonedDateTime startMillis //
    //    /* */, long retryCounts //
    //    /* */) //
    // {
    //     this.startMillis = startMillis;
    //     this.retryCounts = retryCounts;
    // }

    static <W extends PgsqlSnapshot<W>>
    PgsqlSnapshotMetric next //
        /* */( ExecutorService exesvc //
        /* */, W worker //
        /* */, PgConnection pgdata //
        /* */, PgsqlSnapshotMetricSelectRelalist metric
        /* */) //
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
        long start = System.currentTimeMillis();
        Iterator<PgReplRelation> iter = null;
        List<PgsqlRelationMetric> relalist = new ArrayList<>();
        Future<Iterator<PgReplRelation>> future //
            = exesvc.submit(() -> worker.selectRelalist(pgdata).iterator());
        while (worker.newStatus(worker) instanceof SimpleStatusInnerRun) {
            if (iter == null) {
                iter = worker.pollFromFuture(future);
            }
            else if (iter.hasNext()) {
                PgsqlRelationMetric relametric = PgsqlRelationMetric.of(iter.next());
                relalist.add(relametric);
            }
            else {
                ImmutableList<PgsqlRelationMetric> list = ImmutableList.copyOf(relalist);
                long finish = System.currentTimeMillis();
                long oldDuration = finish - start;
                return metric.newLockedRelalist(oldDuration, list);
            }
        }
        pgdata.cancelQuery();
        return metric.newCommit();
    }

    PgsqlSnapshotMetricLockedRelalist
    newLockedRelalist(long oldDuration, ImmutableList<PgsqlRelationMetric> relationLst);

    PgsqlSnapshotMetricCommit newCommit();

    <W extends PgsqlSnapshot<W>>
    PgsqlSnapshotMetric next //
        /* */( ExecutorService exesvc //
        /* */, W worker //
        /* */, PgConnection pgdata //
        /* */); //
}
