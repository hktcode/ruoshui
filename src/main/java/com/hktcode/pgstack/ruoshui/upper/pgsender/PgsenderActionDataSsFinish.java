/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class PgsenderActionDataSsFinish<R, C extends PgsenderConfig<R, C>> //
    extends PgsenderActionData<R, C>
{
    static <R, C extends PgsenderConfig<R, C>> //
    PgsenderActionDataSsFinish<R, C> of(PgsenderActionDataSrFinish<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataSsFinish<>(action);
    }

    static <R, C extends PgsenderConfig<R, C>> //
    PgsenderActionDataSsFinish<R, C> of(PgsenderActionDataSsBegins<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataSsFinish<>(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlotTuple replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssBegins;

    public final PgsenderReportTupleval tupleval;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    private PgsenderActionDataSsFinish(PgsenderActionDataSsBegins<R, C> action)
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

    private PgsenderActionDataSsFinish(PgsenderActionDataSrFinish<R, C> action)
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
    public PgsenderAction<R, C> next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl)
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
        R record = this.config.createMessage(lsn, msg);
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
