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

class PgActionDataSrBegins extends PgActionDataOfferMsg
{
    static PgActionDataSrBegins of(PgActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSrBegins(action);
    }

    static PgActionDataSrBegins of(PgActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSrBegins(action);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relaLock;

    public final PgReportReplSlotTuple replSlot;

    public final PgReportSizeDiff sizeDiff;

    public final PgReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    final PgsqlRelationMetric curRelation;

    private PgActionDataSrBegins(PgActionDataSsBegins action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssbegins = PgReportSsBegins.of(action, action.actionStart);
        this.relationLst = action.relationLst;
        this.relIterator = action.relIterator;
        this.curRelation = this.relIterator.next();
        this.logDatetime = action.logDatetime;
    }

    private PgActionDataSrBegins(PgActionDataSrFinish action)
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
    PgRecord createRecord()
    {
        PgReplRelation r = this.curRelation.relationInfo;
        long lsn = this.replSlot.createTuple.consistentPoint;
        LogicalBegRelationMsg msg = LogicalBegRelationMsg.of(r);
        return this.config.createMessage(lsn, msg);
    }

    @Override
    PgAction complete()
    {
        return PgActionDataTupleval.of(this);
    }

    @Override
    public PgMetricRunTupleval toRunMetrics()
    {
        return PgMetricRunTupleval.of(this);
    }

    @Override
    public PgMetricEndTupleval toEndMetrics()
    {
        return PgMetricEndTupleval.of(this);
    }
}
