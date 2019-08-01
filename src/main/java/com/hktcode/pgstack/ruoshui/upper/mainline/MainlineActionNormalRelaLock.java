/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.postgresql.jdbc.PgConnection;

import java.sql.Statement;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionNormalRelaLock extends MainlineActionNormal
    /* */< MainlineActionNormalRelaLock
    /* */, MainlineConfigNormal
    /* */, MainlineMetricNormalRelaLock
    /* */>
{
    static MainlineActionNormalRelaLock of(MainlineActionNormalRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        long finish = System.currentTimeMillis();
        MainlineReportRelaList relalist = MainlineReportRelaList.of
            /* */( finish - action.metric.actionStart //
            /* */, action.metric.fetchCounts //
            /* */, action.metric.fetchMillis //
            /* */, action.metric.recordCount //
            /* */, action.metric.retryReason //
            /* */);
        MainlineMetricNormalRelaLock metric = MainlineMetricNormalRelaLock.of //
            /* */( action.metric.startMillis
            /* */, relalist //
            /* */, finish //
            /* */, ImmutableList.copyOf(action.metric.relationLst)
            /* */);
        return new MainlineActionNormalRelaLock //
            /* */( action.config //
            /* */, metric //
            /* */, action.tqueue //
            /* */, action.status //
            /* */);
    }

    private MainlineActionNormalRelaLock
        /* */( MainlineConfigNormal config
        /* */, MainlineMetricNormalRelaLock metric
        /* */, TransferQueue<MainlineRecord> tqueue
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        super(config, metric, status, tqueue);
    }

    public MainlineAction next(ExecutorService exesvc, PgConnection pgdata) //
        throws Exception
    {
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        try (Statement s = pgdata.createStatement()) {
            Iterator<PgsqlRelationMetric> iter = this.metric.relationLst.iterator();
            PgsqlRelationMetric relation = null;
            Boolean success = null;
            long starts = System.currentTimeMillis();
            Future<Boolean> executeFuture = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (Boolean.FALSE.equals(success)) {
                    long finish = System.currentTimeMillis();
                    ImmutableList<String> retryReason //
                        = ImmutableList.<String>builder()
                    .addAll(metric.getRelalist.retryReason)
                    .add("lock relation fail").build();
                    MainlineMetricNormalRelaList m = MainlineMetricNormalRelaList.of //
                        (this.metric.startMillis, finish, retryReason);
                    return MainlineActionNormalRelaList.of //
                        (this.config, m, this.status, this.tqueue);
                }
                else if (Boolean.TRUE.equals(success)) {
                    long finish = System.currentTimeMillis();
                    // TODO: 不再需要此方法，记录日志即可
                    relation.lockDuration = (finish - starts);
                    success = null;
                    executeFuture = null;
                }
                else if (executeFuture != null) {
                    success = this.pollFromFuture(executeFuture);
                }
                else if (iter.hasNext()) {
                    relation = iter.next();
                    String sql = this.config.lockRelation(relation.relationInfo, pgdata);
                    Callable<Boolean> callable = MainlineDeputeLockRelation.of(s, sql);
                    starts = System.currentTimeMillis();
                    executeFuture = exesvc.submit(callable);
                }
                else {
                    return MainlineActionNormalReplSlot.of(this);
                }
            }
        }
        return MainlineActionFinish.of();
    }
}
