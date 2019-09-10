/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegRelationMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;

class PgsenderActionDataSrBegins //
    extends PgsenderActionData
{
    static  //
    PgsenderActionDataSrBegins of(PgsenderActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataSrBegins(action);
    }

    static  //
    PgsenderActionDataSrBegins of(PgsenderActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataSrBegins(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlotTuple replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    final PgsqlRelationMetric curRelation;

    private PgsenderActionDataSrBegins(PgsenderActionDataSsBegins action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssbegins = PgsenderReportSsBegins.of(action, action.actionStart);
        this.relationLst = action.relationLst;
        this.relIterator = action.relIterator;
        this.curRelation = this.relIterator.next();
        this.logDatetime = action.logDatetime;
    }

    private PgsenderActionDataSrBegins(PgsenderActionDataSrFinish action) //
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
    public PgsenderAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException
    {
        PgReplRelation r = this.curRelation.relationInfo;
        long lsn = this.replSlot.createTuple.consistentPoint;
        LogicalBegRelationMsg msg = LogicalBegRelationMsg.of(r);
        PgRecord record = this.config.createMessage(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return PgsenderActionDataTupleval.of(this);
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
