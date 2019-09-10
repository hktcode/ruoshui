/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndRelationMsg;
import org.postgresql.jdbc.PgConnection;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;

public class PgActionDataSrFinish extends PgActionData
{
    static PgActionDataSrFinish of(PgActionDataTupleval action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSrFinish(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlotTuple replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    final PgsqlRelationMetric curRelation;

    private PgActionDataSrFinish(PgActionDataTupleval action) //
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
    public PgAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        LogicalEndRelationMsg msg //
            = LogicalEndRelationMsg.of(this.curRelation.relationInfo);
        PgRecord record = this.config.createMessage(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if (record != null) {
                record = this.send(record);
            }
            else if (this.relIterator.hasNext()) {
                return PgActionDataSrBegins.of(this);
            }
            else {
                return PgActionDataSsFinish.of(this);
            }
        }
        return PgActionTerminateEnd.of(this);
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
