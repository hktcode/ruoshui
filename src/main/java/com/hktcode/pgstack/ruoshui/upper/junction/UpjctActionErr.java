/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;

class UpjctActionErr extends SimpleWorker<UpjctAction> implements UpjctAction
{
    public static UpjctActionErr of(UpjctActionRun action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpjctActionErr(action, throwsError);
    }

    public static UpjctActionErr of(UpjctActionEnd action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpjctActionErr(action, throwsError);
    }


    public final TripleJunctionConfig config;

    public final UpjctMetricErr metric;

    private UpjctActionErr(UpjctActionRun action, Throwable throwsError)
    {
        super(action.status, 1);
        this.config = action.config;
        this.metric = UpjctMetricErr.of(action, throwsError);
    }

    private UpjctActionErr(UpjctActionEnd action, Throwable throwsError)
    {
        super(action.status, 1);
        this.config = action.config;
        this.metric = UpjctMetricErr.of(action, throwsError);
    }

    @Override
    public UpjctResultErr get()
    {
        return UpjctResultErr.of(this.config, this.metric);
    }

    @Override
    public UpjctActionErr next(Throwable throwable)
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        // TODO: 将来支持多个Throwable.
        return this;
    }
}
