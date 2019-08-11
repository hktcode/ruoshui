/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricRunBegin1st implements MainlineMetricRun
{
    public static MainlineMetricRunBegin1st of(MainlineActionDataBegin1st action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricRunBegin1st(action);
    }

    public static MainlineMetricRunBegin1st of(MainlineReportBegin1st report)
    {
        if (report == null) {
            throw new ArgumentNullException("report");
        }
        return new MainlineMetricRunBegin1st(report);
    }

    public final MainlineReportBegin1st begin1st;

    private MainlineMetricRunBegin1st(MainlineActionDataBegin1st action)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = MainlineReportBegin1st.of(action, finish);
    }

    private MainlineMetricRunBegin1st(MainlineReportBegin1st begin1st)
    {
        this.begin1st = begin1st;
    }
}
