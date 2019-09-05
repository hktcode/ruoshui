/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;

class UpjctActionEnd extends SimpleWorker<UpjctAction> implements UpjctAction
{
    public static UpjctActionEnd of(UpjctActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UpjctActionEnd(action);
    }

    public final TripleJunctionConfig config;

    public final UpjctMetricEnd metric;

    private UpjctActionEnd(UpjctActionRun action)
    {
        super(action.status, 1);
        this.config = action.config;
        this.metric = UpjctMetricEnd.of(action);
    }

    @Override
    public UpjctResultEnd get()
    {
        return UpjctResultEnd.of(this.config, this.metric);
    }

    @Override
    public UpjctActionErr next(Throwable throwsError)
    {
        return UpjctActionErr.of(this, throwsError);
    }
}
