/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.postgresql.jdbc.PgConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MainlineActionDataSsFinish //
    extends MainlineActionData<MainlineConfigSnapshot>
{
    static MainlineActionDataSsFinish of(MainlineActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataSsFinish(action);
    }

    static MainlineActionDataSsFinish of(MainlineActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataSsFinish(action);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final MainlineReportReplSlotTuple replSlot;

    public final MainlineReportSizeDiff sizeDiff;

    public final MainlineReportSsBegins ssBegins;

    public final MainlineReportTupleval tupleval;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    private MainlineActionDataSsFinish(MainlineActionDataSsBegins action)
    {
        super(action, System.currentTimeMillis());
        this.begin1st = action.begin1st;
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = MainlineReportSsBegins.of(action, this.actionStart);
        this.tupleval = MainlineReportTupleval.of();
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    private MainlineActionDataSsFinish(MainlineActionDataSrFinish action)
    {
        super(action, System.currentTimeMillis());
        this.begin1st = action.begin1st;
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = action.ssbegins;
        this.tupleval = MainlineReportTupleval.of(action, this.actionStart);
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    @Override
    public MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl)
        throws InterruptedException
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
        MainlineRecord record = MainlineRecordNormal.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return MainlineActionDataTypelistSnapshot.of(this);
            }
        }
        return MainlineActionTerminateEnd.of(this);
    }

    @Override
    public MainlineMetricRunSsFinish toRunMetrics()
    {
        return MainlineMetricRunSsFinish.of(this);
    }

    @Override
    public MainlineMetricEndSsFinish toEndMetrics()
    {
        return MainlineMetricEndSsFinish.of(this);
    }
}
