/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleResultErr<A extends BgWorker<A>, C, M extends TripleMetricRun>
    extends TripleResultEnd<A, C, M>
{
    public static <A extends BgWorker<A>, C, M extends TripleMetricRun>
    TripleResultErr<A, C, M> of(C config, TripleMetricErr<M> metric)
    {
        if(config == null){
            throw new ArgumentNullException("config");
        }
        if(metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new TripleResultErr<>(config, metric);
    }

    protected TripleResultErr(C config, TripleMetricErr<M> metric)
    {
        super(config, metric);
    }
}
