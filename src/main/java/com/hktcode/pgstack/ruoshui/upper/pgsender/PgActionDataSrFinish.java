/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndRelationMsg;

import java.util.Iterator;

class PgActionDataSrFinish extends PgActionDataOfferMsg
{
    static PgActionDataSrFinish of(PgActionDataTupleval action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSrFinish(action);
    }

    final PgReportSsBegins ssbegins;

    final Iterator<PgStructRelainfo> relIterator;

    private final PgStructRelainfo curRelation;

    private PgActionDataSrFinish(PgActionDataTupleval action) //
    {
        super(action);
        this.ssbegins = action.ssbegins;
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
    PgRecord createRecord()
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        LogicalEndRelationMsg msg //
            = LogicalEndRelationMsg.of(this.curRelation.relationInfo);
        return PgRecordLogicalMsg.of(lsn, msg);
    }

    @Override
    PgAction complete()
    {
        if (this.relIterator.hasNext()) {
            return PgActionDataSrBegins.of(this);
        }
        else {
            return PgActionDataSsFinish.of(this);
        }
    }

    @Override
    public PgMetricRunTupleval toRunMetrics()
    {
        return PgMetricRunTupleval.of(this);
    }
}
