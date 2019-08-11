/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public abstract class TripleConfig<M extends TripleMetric> //
    extends TqueueConfig implements TripleEntity
{
    public static final long DEFALUT_WAIT_TIMEOUT = 100;

    public static final long DEFAULT_LOG_DURATION = 10 * 1000L;

    // TODO: loggerLevel
    protected TripleConfig()
    {
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
