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

class PgActionDataSsBegins extends PgActionDataSnapshot
{
    static PgActionDataSsBegins of(PgActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSsBegins(action);
    }

    final Iterator<PgsqlRelationMetric> relIterator;

    private PgActionDataSsBegins(PgActionDataSizeDiff action)
    {
        super(action);
        this.relIterator = this.relationLst.iterator();
        this.logDatetime = action.logDatetime;
    }

    @Override
    LogicalMsg createMsg(ImmutableList<PgReplRelation> list)
    {
        return LogicalBegSnapshotMsg.of(list);
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

    @Override
    public PgMetricEndSsbegins toEndMetrics()
    {
        return PgMetricEndSsbegins.of(this);
    }
}
