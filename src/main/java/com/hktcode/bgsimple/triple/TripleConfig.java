/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public abstract class TripleConfig<M extends TripleMetric> implements TripleEntity
{
    public static final long DEFALUT_WAIT_TIMEOUT = 100;

    public static final long DEFAULT_LOG_DURATION = 10 * 1000L;

    public long waitTimeout;

    public long logDuration;

    public abstract M buildMetric(ZonedDateTime startMillis);

    // TODO: loggerLevel
    protected TripleConfig(long waitTimeout, long logDuration)
    {
        this.waitTimeout = waitTimeout;
        this.logDuration = logDuration;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("wait_timeout", this.waitTimeout);
        node.put("log_duration", this.logDuration);
    }
}
