/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegRelationMsg;
import com.hktcode.pgjdbc.PgReplRelation;

import java.util.Iterator;

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

    public final PgReportSsBegins ssbegins;

    final Iterator<PgStructRelainfo> relIterator;

    final PgStructRelainfo curRelation;

    private PgActionDataSrBegins(PgActionDataSsBegins action)
    {
        super(action, System.currentTimeMillis());
        this.ssbegins = PgReportSsBegins.of(action, action.actionStart);
        this.relIterator = action.relIterator;
        this.curRelation = this.relIterator.next();
        this.logDatetime = action.logDatetime;
    }

    private PgActionDataSrBegins(PgActionDataSrFinish action)
    {
        super(action, action.actionStart);
        this.ssbegins = action.ssbegins;
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
        return PgRecordLogicalMsg.of(lsn, msg);
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
}
