/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricErrReplSlot implements PgMetricErr
{
    static PgMetricErrReplSlot of(PgMetricEndReplSlot action, Throwable throwerr)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new PgMetricErrReplSlot(action, throwerr);
    }

    private PgMetricErrReplSlot(PgMetricEndReplSlot action, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relalock;
        this.replslot = action.replslot;
        this.throwerr = PgReportThrowErr.of(finish, throwerr);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;

    public final PgReportThrowErr throwerr;
}
