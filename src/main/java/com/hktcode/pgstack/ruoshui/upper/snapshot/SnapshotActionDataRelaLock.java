/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.mainline.*;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class SnapshotActionDataRelaLock extends SnapshotActionData
{
    private static final Logger logger = LoggerFactory.getLogger(SnapshotActionDataRelaLock.class);

    static SnapshotActionDataRelaLock of(SnapshotActionDataRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataRelaLock(action);
    }

    public final SnapshotReportRelaList relalist;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    private SnapshotActionDataRelaLock(SnapshotActionDataRelaList action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = SnapshotReportRelaList.of(action, this.actionStart);
        this.relationLst = ImmutableList.copyOf(action.relationLst);
        this.logDatetime = action.logDatetime;
    }

    @Override
    SnapshotAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException, InterruptedException, ScriptException
    {
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        try (Statement s = pgdata.createStatement()) {
            Iterator<PgsqlRelationMetric> iter = this.relationLst.iterator();
            PgsqlRelationMetric relation = null;
            Boolean success = null;
            long starts = System.currentTimeMillis();
            Future<Boolean> executeFuture = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (Boolean.FALSE.equals(success)) {
                    return SnapshotActionDataRelaList.of(this);
                }
                else if (Boolean.TRUE.equals(success)) {
                    long finish = System.currentTimeMillis();
                    logger.info("lock relation success: relation={}, duration={}" //
                        , relation.relationInfo, finish - starts);
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
                    logger.info("lock relation: sql={}", sql);
                    Callable<Boolean> callable = MainlineDeputeLockRelation.of(s, sql);
                    starts = System.currentTimeMillis();
                    executeFuture = exesvc.submit(callable);
                }
                else {
                    return SnapshotActionDataReplSlot.of(this);
                }
            }
        }
        return SnapshotActionTerminateEnd.of(this);
    }

    @Override
    public SnapshotMetricRunRelaLock toRunMetrics()
    {
        return SnapshotMetricRunRelaLock.of(this);
    }

    @Override
    public SnapshotMetricEndRelaLock toEndMetrics()
    {
        return SnapshotMetricEndRelaLock.of(this);
    }
}
