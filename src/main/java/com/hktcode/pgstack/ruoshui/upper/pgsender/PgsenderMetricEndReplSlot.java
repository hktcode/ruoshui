/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndReplSlot implements PgsenderMetricEnd
{
    static <R, C extends PgsenderConfig<R, C>>
    PgsenderMetricEndReplSlot of(PgsenderActionDataReplSlot<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndReplSlot(action);
    }

    private <R, C extends PgsenderConfig<R, C>>
    PgsenderMetricEndReplSlot(PgsenderActionDataReplSlot<R, C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = PgsenderReportReplSlot.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportComplete complete;

    @Override
    public PgsenderMetricErrReplSlot toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrReplSlot.of(this, throwerr);
    }
}
