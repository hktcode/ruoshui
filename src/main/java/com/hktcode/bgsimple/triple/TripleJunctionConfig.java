/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class TripleJunctionConfig extends TripleConfig<TripleJunctionMetric>
{
    public static final long DEFALUT_WAIT_TIMEOUT = 100;

    public static final long DEFAULT_LOG_DURATION = 10 * 1000L;

    public static TripleJunctionConfig ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        long waitTimeout = json.path("wait_timeout").asLong(TripleJunctionConfig.DEFALUT_WAIT_TIMEOUT);
        int comeinCount = json.path("comein_count").asInt(100);
        int getoutCount = json.path("getout_count").asInt(100);
        long logDuration = json.path("log_duration").asLong(TripleJunctionConfig.DEFAULT_LOG_DURATION);
        return new TripleJunctionConfig(waitTimeout, comeinCount, getoutCount, logDuration);
    }

    public final int comeinCount;

    public final int getoutCount;

    protected TripleJunctionConfig //
        /* */( long waitTimeout //
        /* */, int comeinCount //
        /* */, int getoutCount //
        /* */, long logDuration //
        /* */) //
    {
        super(waitTimeout, logDuration);
        this.comeinCount = comeinCount;
        this.getoutCount = getoutCount;
    }
}
