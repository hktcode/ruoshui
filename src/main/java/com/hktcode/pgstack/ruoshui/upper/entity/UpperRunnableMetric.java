/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import java.time.ZonedDateTime;

public abstract class UpperRunnableMetric
{
    public final ZonedDateTime startMillis;

    public long recordCount = 0;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    public String statusInfor = "";

    protected UpperRunnableMetric(ZonedDateTime startMillis)
    {
        this.startMillis = startMillis;
    }
}
