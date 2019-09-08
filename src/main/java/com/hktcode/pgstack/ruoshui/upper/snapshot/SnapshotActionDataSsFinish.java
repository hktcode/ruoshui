/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshotLogicalMsg;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

class SnapshotActionDataSsFinish extends SnapshotActionData
{
    static SnapshotActionDataSsFinish of(SnapshotActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataSsFinish(action);
    }

    static SnapshotActionDataSsFinish of(SnapshotActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataSsFinish(action);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relaLock;

    public final SnapshotReportReplSlotTuple replSlot;

    public final SnapshotReportSizeDiff sizeDiff;

    public final SnapshotReportSsBegins ssBegins;

    public final SnapshotReportTupleval tupleval;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    private SnapshotActionDataSsFinish(SnapshotActionDataSsBegins action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = SnapshotReportSsBegins.of(action, this.actionStart);
        this.tupleval = SnapshotReportTupleval.of();
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    private SnapshotActionDataSsFinish(SnapshotActionDataSrFinish action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = action.ssbegins;
        this.tupleval = SnapshotReportTupleval.of(action, this.actionStart);
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    @Override
    SnapshotAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl)
        throws InterruptedException, ScriptException
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
        long lsn = this.replSlot.createTuple.consistentPoint;
        List<PgReplRelation> list = new ArrayList<>(this.relationLst.size());
        for(PgsqlRelationMetric m : this.relationLst) {
            list.add(m.relationInfo);
        }
        ImmutableList<PgReplRelation> l = ImmutableList.copyOf(list);
        LogicalEndSnapshotMsg msg = LogicalEndSnapshotMsg.of(l);
        UpcsmFetchRecordSnapshot record = UpcsmFetchRecordSnapshotLogicalMsg.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                break;
            }
        }
        return SnapshotActionTerminateEnd.of(this);
    }

    @Override
    public SnapshotMetricRunSsFinish toRunMetrics()
    {
        return SnapshotMetricRunSsFinish.of(this);
    }

    @Override
    public SnapshotMetricEndSsFinish toEndMetrics()
    {
        return SnapshotMetricEndSsFinish.of(this);
    }
}
