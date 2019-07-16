/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.*;

/**
 * 创建持久复制槽.
 */
public interface PgsqlSnapshotMetricCreateReplSlot extends PgsqlSnapshotMetricProcess
{
    // public static PgsqlSnapshotMetricCreateReplSlot of //
    //     /* */( PgsqlSnapshotMetricLockedRelalist metric
    //     /* */) //
    // {
    //     return new PgsqlSnapshotMetricCreateReplSlot(metric);
    // }

    // private PgsqlSnapshotMetricCreateReplSlot(PgsqlSnapshotMetricLockedRelalist metric)
    // {
    //     super(metric);
    // }

    static <W extends PgsqlSnapshot<W>>
    PgsqlSnapshotMetric next
        /* */( ExecutorService exesvc
        /* */, W worker
        /* */, PgConnection pgdata
        /* */, PgConnection pgrepl
        /* */, PgsqlSnapshotMetricCreateReplSlot metric
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

        long starts = System.currentTimeMillis();
        worker.sendPauseWorld(metric);
        Future<PgReplSlotTuple> future = exesvc.submit(()-> worker.createReplSlot(pgrepl));
        PgReplSlotTuple tuple = null;
        while (worker.newStatus(worker) instanceof SimpleStatusInnerRun) {
            if (tuple != null) {
                long finish = System.currentTimeMillis();
                long sltDuration = finish - starts;
                return metric.newVerifyRelalist(tuple, sltDuration);
            }
            else {
                tuple = worker.pollFromFuture(future);
            }
        }
        pgdata.cancelQuery();
        return metric.newCommit();
    }

    PgsqlSnapshotMetricVerifyRelalist newVerifyRelalist(PgReplSlotTuple tuple, long sltDuration);
    PgsqlSnapshotMetricCommit newCommit();
}
