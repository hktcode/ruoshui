/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleResultRun<A extends BgWorker<A>, C, M extends TripleMetricRun>
    implements TripleResult<A>
{
    public static <A extends BgWorker<A>, C, M extends TripleMetricRun>
    TripleResultRun<A, C, M> of(C config, M metric)
    {
        if(config == null){
            throw new ArgumentNullException("config");
        }
        if(metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new TripleResultRun<>(config, metric);
    }

    public final C config;

    public final M metric;

    private TripleResultRun(C config, M metric)
    {
        this.config = config;
        this.metric = metric;
    }
}
