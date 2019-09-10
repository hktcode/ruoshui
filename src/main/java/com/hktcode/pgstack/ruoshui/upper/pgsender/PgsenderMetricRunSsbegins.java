/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunSsbegins implements PgsenderMetricRun
{
    static PgsenderMetricRunSsbegins of(PgActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunSsbegins(action);
    }

    private PgsenderMetricRunSsbegins(PgActionDataSsBegins action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = PgsenderReportSsBegins.of(action, finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;

    public final PgsenderReportSsBegins ssbegins;
}
