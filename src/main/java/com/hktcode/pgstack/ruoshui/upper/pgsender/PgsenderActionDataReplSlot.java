/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class PgsenderActionDataReplSlot<R, C extends PgsenderConfig> //
    extends PgsenderActionData<R, C>
{
    private static final Logger logger = LoggerFactory.getLogger(PgsenderActionDataReplSlot.class);

    static <R, C extends PgsenderConfig>
    PgsenderActionDataReplSlot<R, C> of(PgsenderActionDataRelaLock<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataReplSlot<>(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    long sltDuration = 0;

    PgReplSlotTuple[] createTuple = new PgReplSlotTuple[0];

    private PgsenderActionDataReplSlot(PgsenderActionDataRelaLock<R, C> action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = PgsenderReportRelaLock.of(action, this.actionStart);
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    @Override
    public PgsenderAction<R, C> next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl)
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
        long starts = System.currentTimeMillis();
        try(Statement s = pgrepl.createStatement()) {
            PgRecord pauseWorldRecord = this.config.pauseWorldMsg();
            Future<PgReplSlotTuple> tupleFuture = null;
            PgRecord createSlotRecord = null;
            PgReplSlotTuple tuple = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (pauseWorldRecord != null) {
                    pauseWorldRecord = this.send(pauseWorldRecord);
                }
                else if (tupleFuture == null) {
                    Callable<PgReplSlotTuple> callable = this.config.newCreateSlot(s);
                    starts = System.currentTimeMillis();
                    tupleFuture = exesvc.submit(callable);
                }
                else if (tuple == null) {
                    tuple = this.pollFromFuture(tupleFuture);
                }
                else if (this.createTuple.length == 0) {
                    long finish = System.currentTimeMillis();
                    this.sltDuration = finish - starts;
                    logger.info("create slot success");
                    this.createTuple = new PgReplSlotTuple[] { tuple };
                    starts = finish;
                    createSlotRecord = this.config.createSlotMsg(tuple);
                }
                else if (createSlotRecord != null) {
                    createSlotRecord = this.send(createSlotRecord);
                }
                else {
                    long finish = System.currentTimeMillis();
                    logger.info("send create slot success: duration={}" //
                        , finish - starts);
                    return PgsenderActionDataSizeDiff.of(this);
                }
            }
        }
        return PgsenderActionTerminateEnd.of(this);
    }

    @Override
    public PgsenderMetricRunReplSlot toRunMetrics()
    {
        return PgsenderMetricRunReplSlot.of(this);
    }

    @Override
    public PgsenderMetricEndReplSlot toEndMetrics()
    {
        return PgsenderMetricEndReplSlot.of(this);
    }
}
