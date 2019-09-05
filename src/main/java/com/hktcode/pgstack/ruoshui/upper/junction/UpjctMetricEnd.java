/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpjctMetricEnd
{
    public static UpjctMetricEnd of(UpjctActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UpjctMetricEnd(action);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long totalMillis;

    public final long curLsnofcmt;

    public final long curSequence;

    public final String statusInfor;

    private UpjctMetricEnd(UpjctActionRun action)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.curLsnofcmt = action.curLsnofcmt;
        this.curSequence = action.curSequence;
        this.statusInfor = action.statusInfor;
        this.totalMillis = finish - action.actionStart;
    }
}
