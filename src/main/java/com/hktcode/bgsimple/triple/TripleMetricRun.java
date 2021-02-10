/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.tqueue.TqueueConfig;

public class TripleMetricRun
{
    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long totalMillis;

    /**
     * 描述当前状态的信息.
     */
    public final String statusInfor;

    protected <C extends TqueueConfig, M extends TripleMetricRun> //
    TripleMetricRun(TripleActionRun<C, M> action)
    {
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.statusInfor = action.statusInfor;
        final long finish = System.currentTimeMillis();
        this.totalMillis = finish - actionStart;
    }
}
