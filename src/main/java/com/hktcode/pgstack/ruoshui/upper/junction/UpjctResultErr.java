/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpjctResultErr implements UpjctResult
{
    public static UpjctResultErr //
    of(TripleJunctionConfig config, UpjctMetricErr metric)
    {
        if(config == null){
            throw new ArgumentNullException("config");
        }
        if(metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpjctResultErr(config, metric);
    }

    public final TripleJunctionConfig config;

    public final UpjctMetricErr metric;

    private UpjctResultErr(TripleJunctionConfig config, UpjctMetricErr metric)
    {
        this.config = config;
        this.metric = metric;
    }
}
