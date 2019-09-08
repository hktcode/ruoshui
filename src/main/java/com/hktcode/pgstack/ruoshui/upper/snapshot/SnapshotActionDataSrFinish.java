/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndRelationMsg;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshotLogicalMsg;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

class SnapshotActionDataSrFinish extends SnapshotActionData
{
    static SnapshotActionDataSrFinish of(SnapshotActionDataTupleval action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataSrFinish(action);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relaLock;

    public final SnapshotReportReplSlotTuple replSlot;

    public final SnapshotReportSizeDiff sizeDiff;

    public final SnapshotReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    final PgsqlRelationMetric curRelation;

    private SnapshotActionDataSrFinish(SnapshotActionDataTupleval action) //
    {
        super(action, action.actionStart);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.relationLst = action.relationLst;
        this.relIterator = action.relIterator;
        this.logDatetime = action.logDatetime;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.offerMillis = action.offerMillis;
        this.offerCounts = action.offerCounts;
        this.rsnextCount = action.rsnextCount;
        this.recordCount = action.recordCount;
        this.curRelation = action.curRelation;
    }

    @Override
    public SnapshotAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException, ScriptException
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        LogicalEndRelationMsg msg //
            = LogicalEndRelationMsg.of(this.curRelation.relationInfo);
        UpcsmFetchRecordSnapshot record = UpcsmFetchRecordSnapshotLogicalMsg.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if (record != null) {
                record = this.send(record);
            }
            else if (this.relIterator.hasNext()) {
                return SnapshotActionDataSrBegins.of(this);
            }
            else {
                return SnapshotActionDataSsFinish.of(this);
            }
        }
        return SnapshotActionTerminateEnd.of(this);
    }

    @Override
    public SnapshotMetricRunTupleval toRunMetrics()
    {
        return SnapshotMetricRunTupleval.of(this);
    }

    @Override
    public SnapshotMetricEndTupleval toEndMetrics()
    {
        return SnapshotMetricEndTupleval.of(this);
    }
}
