/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricRunTypelistContinue extends PgMetricRunTypelist
{
    static PgMetricRunTypelistContinue of(PgActionDataTypelistContinue action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricRunTypelistContinue(action);
    }

    private PgMetricRunTypelistContinue(PgActionDataTypelistContinue action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;
}
