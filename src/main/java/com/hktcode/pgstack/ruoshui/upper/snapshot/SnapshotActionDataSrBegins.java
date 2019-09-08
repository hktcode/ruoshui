/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegRelationMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshotLogicalMsg;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

class SnapshotActionDataSrBegins extends SnapshotActionData
{
    static SnapshotActionDataSrBegins of(SnapshotActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataSrBegins(action);
    }

    static SnapshotActionDataSrBegins of(SnapshotActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataSrBegins(action);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relaLock;

    public final SnapshotReportReplSlotTuple replSlot;

    public final SnapshotReportSizeDiff sizeDiff;

    public final SnapshotReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    final PgsqlRelationMetric curRelation;

    private SnapshotActionDataSrBegins(SnapshotActionDataSsBegins action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssbegins = SnapshotReportSsBegins.of(action, action.actionStart);
        this.relationLst = action.relationLst;
        this.relIterator = action.relIterator;
        this.curRelation = this.relIterator.next();
        this.logDatetime = action.logDatetime;
    }

    private SnapshotActionDataSrBegins(SnapshotActionDataSrFinish action) //
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
        this.curRelation = action.relIterator.next();
    }


    @Override
    public SnapshotAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException, ScriptException
    {
        PgReplRelation r = this.curRelation.relationInfo;
        long lsn = this.replSlot.createTuple.consistentPoint;
        LogicalBegRelationMsg msg = LogicalBegRelationMsg.of(r);
        UpcsmFetchRecordSnapshot record = UpcsmFetchRecordSnapshotLogicalMsg.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return SnapshotActionDataTupleval.of(this);
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
