/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpjctResultEnd implements UpjctResult
{
    public static UpjctResultEnd //
    of(TripleJunctionConfig config, UpjctMetricEnd metric)
    {
        if(config == null){
            throw new ArgumentNullException("config");
        }
        if(metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpjctResultEnd(config, metric);
    }

    public final TripleJunctionConfig config;

    public final UpjctMetricEnd metric;

    private UpjctResultEnd(TripleJunctionConfig config, UpjctMetricEnd metric)
    {
        this.config = config;
        this.metric = metric;
    }
}
