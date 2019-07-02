/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.naive;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class NaiveJunctionConfig extends NaiveConfig
{
    public static final long DEFALUT_WAIT_TIMEOUT = 100;

    public static final long DEFAULT_LOG_DURATION = 10 * 1000L;

    public static NaiveJunctionConfig ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        long waitTimeout = json.path("wait_timeout").asLong(NaiveJunctionConfig.DEFALUT_WAIT_TIMEOUT);
        int comeinCount = json.path("comein_count").asInt(100);
        int getoutCount = json.path("getout_count").asInt(100);
        long logDuration = json.path("log_duration").asLong(NaiveJunctionConfig.DEFAULT_LOG_DURATION);
        return new NaiveJunctionConfig(waitTimeout, comeinCount, getoutCount, logDuration);
    }

    public final int comeinCount;

    public final int getoutCount;

    protected NaiveJunctionConfig //
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
