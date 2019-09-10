/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class PgsenderActionDataSsFinish //
    extends PgsenderActionData
{
    static  //
    PgsenderActionDataSsFinish of(PgsenderActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataSsFinish(action);
    }

    static  //
    PgsenderActionDataSsFinish of(PgsenderActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataSsFinish(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlotTuple replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssBegins;

    public final PgsenderReportTupleval tupleval;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    private PgsenderActionDataSsFinish(PgsenderActionDataSsBegins action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = PgsenderReportSsBegins.of(action, this.actionStart);
        this.tupleval = PgsenderReportTupleval.of();
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    private PgsenderActionDataSsFinish(PgsenderActionDataSrFinish action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, this.actionStart);
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    @Override
    public PgsenderAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl)
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
        PgRecord record = this.config.createMessage(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return this.config.afterSnapshot(this);
            }
        }
        return PgsenderActionTerminateEnd.of(this);
    }

    @Override
    public PgsenderMetricRunSsFinish toRunMetrics()
    {
        return PgsenderMetricRunSsFinish.of(this);
    }

    @Override
    public PgsenderMetricEndSsFinish toEndMetrics()
    {
        return PgsenderMetricEndSsFinish.of(this);
    }
}
