/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshotLogicalMsg;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

class SnapshotActionDataSsBegins extends SnapshotActionData
{
    static SnapshotActionDataSsBegins of(SnapshotActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataSsBegins(action);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relaLock;

    public final SnapshotReportReplSlotTuple replSlot;

    public final SnapshotReportSizeDiff sizeDiff;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    private SnapshotActionDataSsBegins(SnapshotActionDataSizeDiff action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = SnapshotReportSizeDiff.of(action, this.actionStart);
        this.relationLst = action.oldRelalist;
        this.relIterator = this.relationLst.iterator();
        this.logDatetime = action.logDatetime;
    }

    @Override
    SnapshotAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException, ScriptException
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        List<PgReplRelation> list = new ArrayList<>(this.relationLst.size());
        for(PgsqlRelationMetric m : this.relationLst) {
            list.add(m.relationInfo);
        }
        ImmutableList<PgReplRelation> l = ImmutableList.copyOf(list);
        LogicalBegSnapshotMsg msg = LogicalBegSnapshotMsg.of(l);
        UpcsmFetchRecordSnapshot record = UpcsmFetchRecordSnapshotLogicalMsg.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if (record != null) {
                record = this.send(record);
            }
            else if (this.relIterator.hasNext()){
                return SnapshotActionDataSrBegins.of(this);
            }
            else {
                return SnapshotActionDataSsFinish.of(this);
            }
        }
        return SnapshotActionTerminateEnd.of(this);
    }

    @Override
    public SnapshotMetricRunSsbegins toRunMetrics()
    {
        return SnapshotMetricRunSsbegins.of(this);
    }

    @Override
    public SnapshotMetricEndSsbegins toEndMetrics()
    {
        return SnapshotMetricEndSsbegins.of(this);
    }
}
