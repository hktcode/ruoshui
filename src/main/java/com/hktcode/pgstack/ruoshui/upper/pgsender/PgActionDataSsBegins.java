/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegSnapshotMsg;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.PgReplRelation;

import java.util.Iterator;

class PgActionDataSsBegins extends PgActionDataOfferMsg
{
    static PgActionDataSsBegins of(PgActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSsBegins(action);
    }

    final Iterator<PgStructRelainfo> relIterator;

    private PgActionDataSsBegins(PgActionDataSizeDiff action)
    {
        super(action);
        this.relIterator = this.relationLst.iterator();
        this.logDatetime = action.logDatetime;
    }

    @Override
    PgRecord createRecord()
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        ImmutableList<PgReplRelation> list = super.getImmutableReplRelaList();
        LogicalMsg msg = LogicalBegSnapshotMsg.of(list);
        return PgRecordLogicalMsg.of(lsn, msg);
    }

    @Override
    PgAction complete()
    {
        if (this.relIterator.hasNext()){
            return PgActionDataSrBegins.of(this);
        }
        else {
            return PgActionDataSsFinish.of(this);
        }
    }

    @Override
    public PgMetricRunSsbegins toRunMetrics()
    {
        return PgMetricRunSsbegins.of(this);
    }
}
