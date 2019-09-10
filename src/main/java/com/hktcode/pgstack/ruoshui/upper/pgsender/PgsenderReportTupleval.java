/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportTupleval
{
    static PgsenderReportTupleval of()
    {
        return new PgsenderReportTupleval();
    }

    static <C extends PgsenderConfig>
    PgsenderReportTupleval of(PgsenderActionDataSrBegins<C> action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportTupleval(action, finish);
    }

    static <C extends PgsenderConfig> //
    PgsenderReportTupleval of(PgsenderActionDataTupleval<C> action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportTupleval(action, finish);
    }

    static <C extends PgsenderConfig> //
    PgsenderReportTupleval of(PgsenderActionDataSrFinish<C> action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportTupleval(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long offerCounts;

    public final long offerMillis;

    public final long recordCount;

    private PgsenderReportTupleval()
    {
        this.totalMillis = 0;
        this.rsgetCounts = 0;
        this.rsgetMillis = 0;
        this.rsnextCount = 0;
        this.offerCounts = 0;
        this.offerMillis = 0;
        this.recordCount = 0;
    }

    private <C extends PgsenderConfig> //
    PgsenderReportTupleval(PgsenderActionDataSrBegins<C> action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
    }

    private <C extends PgsenderConfig> //
    PgsenderReportTupleval(PgsenderActionDataTupleval<C> action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
    }

    private <C extends PgsenderConfig> //
    PgsenderReportTupleval(PgsenderActionDataSrFinish<C> action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
    }
}
