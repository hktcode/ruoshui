/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.naive;

import java.time.ZonedDateTime;

public class NaiveMutableMetric
{
    public final ZonedDateTime startMillis;

    public long recordCount = 0;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    /**
     * 描述当前状态的信息.
     */
    public String statusInfor = "";

    protected NaiveMutableMetric(ZonedDateTime startMillis)
    {
        this.startMillis = startMillis;
    }
}
