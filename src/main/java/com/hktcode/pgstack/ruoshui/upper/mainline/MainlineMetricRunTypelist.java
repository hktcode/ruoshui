/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public abstract class MainlineMetricRunTypelist implements MainlineMetricRun
{
    protected MainlineMetricRunTypelist(MainlineActionDataTypelist action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = MainlineReportTypelist.of(action, finish);
    }

    public final MainlineReportTypelist typelist;
}
