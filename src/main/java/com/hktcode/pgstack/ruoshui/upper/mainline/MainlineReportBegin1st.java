/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineReportBegin1st implements MainlineReport
{
    public final long startMillis;

    public final long totalMillis;

    static MainlineReportBegin1st of(long startMillis, long totalMillis)
    {
        return new MainlineReportBegin1st(startMillis, totalMillis);
    }

    static MainlineReportBegin1st of(MainlineActionDataBegin1st begin1st, long finish)
    {
        if (begin1st == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineReportBegin1st(begin1st, finish);
    }

    private MainlineReportBegin1st(long startMillis, long totalMillis)
    {
        this.startMillis = startMillis;
        this.totalMillis = totalMillis;
    }

    private MainlineReportBegin1st(MainlineActionDataBegin1st begin1st, long finish)
    {
        this.startMillis = begin1st.startMillis;
        this.totalMillis = finish - begin1st.startMillis;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("start_millis", startMillis);
        node.put("total_millis", totalMillis);
    }
}
