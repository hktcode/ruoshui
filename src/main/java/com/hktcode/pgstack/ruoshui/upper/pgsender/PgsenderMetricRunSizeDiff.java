/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunSizeDiff implements PgsenderMetricRun
{
    static <R, C extends PgsenderConfig> //
    PgsenderMetricRunSizeDiff of(PgsenderActionDataSizeDiff<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunSizeDiff(action);
    }

    private <R, C extends PgsenderConfig> //
    PgsenderMetricRunSizeDiff(PgsenderActionDataSizeDiff<R, C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = PgsenderReportSizeDiff.of(action, finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;
}
