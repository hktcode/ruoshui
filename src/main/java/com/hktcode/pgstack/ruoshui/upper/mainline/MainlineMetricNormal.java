/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;

import java.time.ZonedDateTime;

public class MainlineMetricNormal
{
    public final long startMillis;

    public long recordCount = 0;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    public String statusInfor = "";

    protected MainlineMetricNormal(long startMillis)
    {
        this.startMillis = startMillis;
    }
}
