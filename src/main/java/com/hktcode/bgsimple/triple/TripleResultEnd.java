/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleResultEnd<A extends BgWorker<A>, C, M extends TripleMetricRun>
    implements TripleResult<A>, SimpleMethodAllResultEnd<A>
{
    public static <A extends BgWorker<A>, C, M extends TripleMetricRun>
    TripleResultEnd<A, C, M> of(C config, TripleMetricEnd<M> metric)
    {
        if(config == null){
            throw new ArgumentNullException("config");
        }
        if(metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new TripleResultEnd<>(config, metric);
    }

    public final C config;

    public final TripleMetricEnd<M> metric;

    protected TripleResultEnd(C config, TripleMetricEnd<M> metric)
    {
        this.config = config;
        this.metric = metric;
    }
}
