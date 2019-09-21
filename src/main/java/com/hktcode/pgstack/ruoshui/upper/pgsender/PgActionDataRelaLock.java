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

class PgActionDataRelaLock extends PgActionData
{
    private static final Logger logger = LoggerFactory.getLogger(PgActionDataRelaLock.class);

    static PgActionDataRelaLock of(PgActionDataRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataRelaLock(action);
    }

    public final PgReportRelaList relalist;

    public final ImmutableList<PgStructRelainfo> relationLst;

    private PgActionDataRelaLock(PgActionDataRelaList action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = PgReportRelaList.of(action, this.actionStart);
        this.relationLst = ImmutableList.copyOf(action.newRelalist);
        this.logDatetime = action.logDatetime;
    }

    @Override
    public PgAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
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
            Iterator<PgStructRelainfo> iter = this.relationLst.iterator();
            PgStructRelainfo relation = null;
            Boolean success = null;
            long starts = System.currentTimeMillis();
            Future<Boolean> executeFuture = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (Boolean.FALSE.equals(success)) {
                    pgdata.rollback();
                    return PgActionDataRelaList.of(this);
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
                    Callable<Boolean> callable = this.createLock(relation, s);
                    starts = System.currentTimeMillis();
                    executeFuture = exesvc.submit(callable);
                }
                else {
                    return PgActionDataReplSlotSnapshot.of(this);
                }
            }
        }
        return PgActionTerminateEnd.of(this);
    }

    private Callable<Boolean> createLock(PgStructRelainfo relation, Statement s)
        throws SQLException
    {
        PgLockMode lock = this.config.lockingMode;
        if (lock == PgLockMode.NO_LOCK || lock == PgLockMode.NULL_LOCK) {
            return ()->true;
        }
        String dbschema = relation.relationInfo.dbschema;
        String relaname = relation.relationInfo.relation;
        PgConnection connection = s.getConnection().unwrap(PgConnection.class);
        String sql = lock.lockStatement(connection, dbschema, relaname);
        logger.info("lock relation: sql={}", sql);
        return DeputeLockRelationMainline.of(s, sql);
    }

    @Override
    public PgMetricRunRelaLock toRunMetrics()
    {
        return PgMetricRunRelaLock.of(this);
    }
}
