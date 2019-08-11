/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public abstract class MainlineMetricRunTypelist implements MainlineMetricRun
{
    protected MainlineMetricRunTypelist(MainlineActionDataTypelist action)
    {
        this.begin1st = action.begin1st;
        long finish = System.currentTimeMillis();
        this.typelist = MainlineReportTypelist.of(action, finish);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportTypelist typelist;
}
