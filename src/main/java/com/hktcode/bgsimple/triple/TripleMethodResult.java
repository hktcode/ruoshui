/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleMethodResult
    /* */< W extends TripleWorker<W, F, M> //
    /* */, F extends TripleConfig //
    /* */, M extends TripleMetric //
    /* */>
    implements SimpleMethodAllResult<W, M>
{
    public static <W extends TripleWorker<W, F, M>, F extends TripleConfig, M extends TripleMetric>
    TripleMethodResult<W, F, M> of(JsonNode config, JsonNode metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new TripleMethodResult<>(config, metric);
    }

    public final JsonNode config;

    public final JsonNode metric;

    private TripleMethodResult(JsonNode config, JsonNode metric)
    {
        this.config = config;
        this.metric = metric;
    }
}
