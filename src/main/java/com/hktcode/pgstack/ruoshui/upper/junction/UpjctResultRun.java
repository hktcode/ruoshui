/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpjctResultRun implements UpjctResult
{
    public static UpjctResultRun //
    of(TripleJunctionConfig config, UpjctMetricRun metric)
    {
        if(config == null){
            throw new ArgumentNullException("config");
        }
        if(metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpjctResultRun(config, metric);
    }

    public final TripleJunctionConfig config;

    public final UpjctMetricRun metric;

    private UpjctResultRun(TripleJunctionConfig config, UpjctMetricRun metric)
    {
        this.config = config;
        this.metric = metric;
    }
}
