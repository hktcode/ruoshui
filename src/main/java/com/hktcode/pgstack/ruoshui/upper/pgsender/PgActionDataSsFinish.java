/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndSnapshotMsg;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.PgReplRelation;

class PgActionDataSsFinish extends PgActionDataOfferMsg
{
    static PgActionDataSsFinish of(PgActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSsFinish(action);
    }

    static PgActionDataSsFinish of(PgActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSsFinish(action);
    }

    final PgReportSsBegins ssBegins;

    final PgReportTupleval tupleval;

    private PgActionDataSsFinish(PgActionDataSsBegins action)
    {
        super(action);
        this.ssBegins = PgReportSsBegins.of(action, this.actionStart);
        this.tupleval = PgReportTupleval.of();
        this.logDatetime = action.logDatetime;
    }

    private PgActionDataSsFinish(PgActionDataSrFinish action)
    {
        super(action);
        this.ssBegins = action.ssbegins;
        this.tupleval = PgReportTupleval.of(action, this.actionStart);
        this.logDatetime = action.logDatetime;
    }

    @Override
    PgRecord createRecord()
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        ImmutableList<PgReplRelation> list = super.getImmutableReplRelaList();
        LogicalMsg msg = LogicalEndSnapshotMsg.of(list);
        return PgRecordLogicalMsg.of(lsn, msg);
    }

    @Override
    PgAction complete()
    {
        return this.config.afterSnapshot(this);
    }

    @Override
    public PgMetricRunSsFinish toRunMetrics()
    {
        return PgMetricRunSsFinish.of(this);
    }
}
