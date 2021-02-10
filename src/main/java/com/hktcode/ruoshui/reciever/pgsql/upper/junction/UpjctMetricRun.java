/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.hktcode.bgsimple.triple.TripleMetricRun;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpjctMetricRun extends TripleMetricRun
{
    public static UpjctMetricRun of(UpjctActionRun action)
    {
        if(action == null) {
            throw new ArgumentNullException("action");
        }
        return new UpjctMetricRun(action);
    }

    public final long curLsnofcmt;

    public final long curSequence;

    private UpjctMetricRun(UpjctActionRun action)
    {
        super(action);
        this.curLsnofcmt = action.curLsnofcmt;
        this.curSequence = action.curSequence;
    }
}
