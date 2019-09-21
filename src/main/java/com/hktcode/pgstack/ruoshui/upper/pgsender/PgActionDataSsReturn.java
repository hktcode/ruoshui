/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

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

    final PgReportSsBegins ssBegins;

    final PgReportTupleval tupleval;

    final PgReportSsFinish ssfinish;

    private PgActionDataSsReturn(PgActionDataSsFinish action)
    {
        super(action);
        long finish = System.currentTimeMillis();
        this.ssBegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = PgReportSsFinish.of(action, finish);
        this.logDatetime = action.logDatetime;
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
