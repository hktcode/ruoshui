/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class PgsenderActionDataRelaLock<R, C extends PgsenderConfig> //
    extends PgsenderActionData<R, C>
{
    private static final Logger logger = LoggerFactory.getLogger(PgsenderActionDataRelaLock.class);

    static <R, C extends PgsenderConfig>  //
    PgsenderActionDataRelaLock<R, C> of(PgsenderActionDataRelaList<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataRelaLock<>(action);
    }

    public final PgsenderReportRelaList relalist;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    private PgsenderActionDataRelaLock(PgsenderActionDataRelaList<R, C> action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = PgsenderReportRelaList.of(action, this.actionStart);
        this.relationLst = ImmutableList.copyOf(action.relationLst);
        this.logDatetime = action.logDatetime;
    }

    @Override
    public PgsenderAction<R, C> next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException, InterruptedException
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
                    return PgsenderActionDataRelaList.of(this);
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
                    String sql = this.config.lockStatement(relation.relationInfo, pgdata);
                    logger.info("lock relation: sql={}", sql);
                    Callable<Boolean> callable = DeputeLockRelationMainline.of(s, sql);
                    starts = System.currentTimeMillis();
                    executeFuture = exesvc.submit(callable);
                }
                else {
                    return PgsenderActionDataReplSlot.of(this);
                }
            }
        }
        return PgsenderActionTerminateEnd.of(this);
    }

    @Override
    public PgsenderMetricRunRelaLock toRunMetrics()
    {
        return PgsenderMetricRunRelaLock.of(this);
    }

    @Override
    public PgsenderMetricEndRelaLock toEndMetrics()
    {
        return PgsenderMetricEndRelaLock.of(this);
    }
}
