/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndRelationMsg;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

public class PgsenderActionDataSrFinish<R, C extends PgsenderConfig<R, C>> //
    extends PgsenderActionData<R, C>
{
    static <R, C extends PgsenderConfig<R, C>> //
    PgsenderActionDataSrFinish<R, C> of(PgsenderActionDataTupleval<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataSrFinish<>(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlotTuple replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    final PgsqlRelationMetric curRelation;

    private PgsenderActionDataSrFinish(PgsenderActionDataTupleval<R, C> action) //
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
    public PgsenderAction<R, C> next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException, ScriptException
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        LogicalEndRelationMsg msg //
            = LogicalEndRelationMsg.of(this.curRelation.relationInfo);
        R record = this.config.createMessage(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if (record != null) {
                record = this.send(record);
            }
            else if (this.relIterator.hasNext()) {
                return PgsenderActionDataSrBegins.of(this);
            }
            else {
                return PgsenderActionDataSsFinish.of(this);
            }
        }
        return PgsenderActionTerminateEnd.of(this);
    }

    @Override
    public PgsenderMetricRunTupleval toRunMetrics()
    {
        return PgsenderMetricRunTupleval.of(this);
    }

    @Override
    public PgsenderMetricEndTupleval toEndMetrics()
    {
        return PgsenderMetricEndTupleval.of(this);
    }
}
