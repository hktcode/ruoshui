/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricEndBegin1st implements MainlineMetricEnd
{
    public static MainlineMetricEndBegin1st of(MainlineActionDataBegin1st action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricEndBegin1st(action);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportComplete complete;

    private MainlineMetricEndBegin1st(MainlineActionDataBegin1st action)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = MainlineReportBegin1st.of(action, finish);
        this.complete = MainlineReportComplete.of(finish);
    }

    @Override
    public MainlineMetricErrBegin1st toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return MainlineMetricErrBegin1st.of(this, throwerr);
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode begin1stNode = node.putObject("begin1st");
        this.begin1st.toJsonObject(begin1stNode);
        ObjectNode completeNode = node.putObject("complete");
        this.complete.toJsonObject(completeNode);
    }
}
