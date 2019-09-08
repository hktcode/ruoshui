/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshotCreateSlot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshotPauseWorld;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class SnapshotActionDataReplSlot extends SnapshotActionData
{
    private static final Logger logger = LoggerFactory.getLogger(SnapshotActionDataReplSlot.class);

    static SnapshotActionDataReplSlot of(SnapshotActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataReplSlot(action);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relaLock;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    long sltDuration = 0;

    PgReplSlotTuple[] createTuple = new PgReplSlotTuple[0];

    private SnapshotActionDataReplSlot(SnapshotActionDataRelaLock action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = SnapshotReportRelaLock.of(action, this.actionStart);
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    @Override
    SnapshotAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl)
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
        long starts = System.currentTimeMillis();
        try(Statement s = pgrepl.createStatement()) {
            boolean sendPauseWorld = this.sendPauseWorld();
            Future<PgReplSlotTuple> tupleFuture = null;
            Boolean sendCreateSlot = null;
            PgReplSlotTuple tuple = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (!sendPauseWorld) {
                    sendPauseWorld = this.sendPauseWorld();
                }
                else if (tupleFuture == null) {
                    String slotname = this.config.logicalRepl.slotName;
                    SnapshotDeputeCreateReplSlot callable = SnapshotDeputeCreateReplSlot.of(s, slotname);
                    starts = System.currentTimeMillis();
                    tupleFuture = exesvc.submit(callable);
                }
                else if (tuple == null) {
                    tuple = this.pollFromFuture(tupleFuture);
                }
                else if (sendCreateSlot == null) {
                    long finish = System.currentTimeMillis();
                    this.sltDuration = finish - starts;
                    logger.info("create slot success");
                    this.createTuple = new PgReplSlotTuple[] { tuple };
                    starts = finish;
                    sendCreateSlot = this.sendCreateSlot(tuple);
                }
                else if (!sendCreateSlot) {
                    sendCreateSlot = this.sendCreateSlot(tuple);
                }
                else {
                    long finish = System.currentTimeMillis();
                    logger.info("send create slot success: duration={}" //
                        , finish - starts);
                    return SnapshotActionDataSizeDiff.of(this);
                }
            }
        }
        return SnapshotActionTerminateEnd.of(this);
    }

    public boolean sendPauseWorld() throws InterruptedException
    {
        UpcsmFetchRecordSnapshotPauseWorld record = UpcsmFetchRecordSnapshotPauseWorld.of();
        return this.tqueue.tryTransfer(record, this.config.waitTimeout, TimeUnit.MILLISECONDS);
    }

    public boolean sendCreateSlot(PgReplSlotTuple tuple) throws InterruptedException
    {
        if (tuple == null) {
            throw new ArgumentNullException("tuple");
        }
        UpcsmFetchRecordSnapshotCreateSlot record = UpcsmFetchRecordSnapshotCreateSlot.of(tuple);
        return tqueue.tryTransfer(record, config.waitTimeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public SnapshotMetricRunReplSlot toRunMetrics()
    {
        return SnapshotMetricRunReplSlot.of(this);
    }

    @Override
    public SnapshotMetricEndReplSlot toEndMetrics()
    {
        return SnapshotMetricEndReplSlot.of(this);
    }
}
