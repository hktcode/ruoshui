/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 验证中间是否有新增关系.
 */
public interface PgsqlSnapshotMetricVerifyRelalist extends PgsqlSnapshotMetricProcess
{
    // public static PgsqlSnapshotMetricVerifyRelalist of //
    //     /* */( PgsqlSnapshotMetricCreateReplSlot metric //
    //     /* */, PgReplSlotTuple sltTupleval //
    //     /* */, long sltDuration //
    //     /* */) //
    // {
    //     return new PgsqlSnapshotMetricVerifyRelalist(metric, sltDuration, sltTupleval);
    // }

    // public final PgReplSlotTuple sltTupleval;

    // public final long sltDuration;

    // protected PgsqlSnapshotMetricVerifyRelalist
    //     /* */( PgsqlSnapshotMetricCreateReplSlot metric
    //     /* */, long sltDuration
    //     /* */, PgReplSlotTuple sltTupleval
    //     /* */)
    // {
    //     super(metric);
    //     this.sltTupleval = sltTupleval;
    //     this.sltDuration = sltDuration;
    // }

    // protected PgsqlSnapshotMetricVerifyRelalist(PgsqlSnapshotMetricVerifyRelalist metric)
    // {
    //     super(metric);
    //     this.sltTupleval = metric.sltTupleval;
    //     this.sltDuration = metric.sltDuration;
    // }

    static final Logger logger = LoggerFactory.getLogger(PgsqlSnapshotMetricVerifyRelalist.class);

    static <W extends SimpleWorker<W, PgsqlSnapshotMetric> & PgsqlSnapshot<W>>
    PgsqlSnapshotMetric next
        /* */( ExecutorService exesvc
        /* */, W worker
        /* */, PgConnection pgdata
        /* */, PgConnection pgrepl
        /* */, PgReplSlotTuple sltTupleval
        /* */, PgsqlSnapshotMetricVerifyRelalist metric
        /* */, List<PgsqlRelationMetric> oldlist
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
        // Object newscriptMs;
        // Object newselectMs;
        String setTransaction = "" //
            + "SET TRANSACTION SNAPSHOT '" //
            + pgdata.escapeLiteral(sltTupleval.snapshotName) //
            + "'";
        try (Statement statement = pgdata.createStatement()) {
            statement.execute(setTransaction);
        }

        long starts = System.currentTimeMillis();
        Future<ImmutableList<PgReplRelation>> future = exesvc.submit(()->worker.selectRelalist(pgdata));
        ImmutableList<PgReplRelation> list = null;
        while (worker.newStatus(worker, metric) instanceof SimpleStatusInnerRun) {
            if (list == null) {
                list = worker.pollFromFuture(future);
            }
            else if (list.size() == oldlist.size()) {
                long finish = System.currentTimeMillis();
                long newDuration = finish - starts;
                return metric.newSelectTupleval(newDuration);
            }
            else {
                try (Statement s = pgrepl.createStatement()) {
                    String slotname = pgrepl.escapeIdentifier(sltTupleval.slotName);
                    s.execute("DROP_REPLICATION_SLOT " + slotname);
                }
                ImmutableList.Builder<PgReplRelation> builder //
                    = ImmutableList.builderWithExpectedSize(oldlist.size());
                for (PgsqlRelationMetric r : oldlist) {
                    builder.add(r.relationInfo);
                }
                ImmutableList<PgReplRelation> oldRelalist = builder.build();
                return metric.newAbortsDiffSize(oldRelalist, list);
            }
        }
        pgdata.cancelQuery();
        return metric.newCommit();
    }

    PgsqlSnapshotMetricSelectTupleval newSelectTupleval(long newDuration);

    PgsqlSnapshotMetricAbortsDiffSize newAbortsDiffSize(ImmutableList<PgReplRelation> oldRelalist, ImmutableList<PgReplRelation> newRelalist);

    PgsqlSnapshotMetricCommit newCommit();
}
