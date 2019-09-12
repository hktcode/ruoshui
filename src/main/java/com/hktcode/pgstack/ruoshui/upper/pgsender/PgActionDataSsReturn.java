/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

class PgActionDataSsReturn extends PgActionDataOfferMsg
{
    static PgActionDataSsReturn of(PgActionDataSsFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSsReturn(action);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relaLock;

    public final PgReportReplSlotTuple replSlot;

    public final PgReportSizeDiff sizeDiff;

    final ImmutableList<PgStructRelainfo> relationLst;

    public final PgReportSsBegins ssBegins;

    public final PgReportTupleval tupleval;

    public final PgReportSsFinish ssfinish;

    private PgActionDataSsReturn(PgActionDataSsFinish action)
    {
        super(action);
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = PgReportSsFinish.of(action, finish);
        this.logDatetime = action.logDatetime;
        this.relationLst = action.relationLst;
    }

    @Override
    PgRecord createRecord()
    {
        return PgRecordExecFinish.of(this.del());
    }

    @Override
    PgAction complete()
    {
        return PgActionTerminateEnd.of(this);
    }

    @Override
    public PgMetricRunSsFinish toRunMetrics()
    {
        return PgMetricRunSsFinish.of(this);
    }
}
