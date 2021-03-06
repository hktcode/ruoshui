/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public abstract class SimpleConfig<C extends SimpleConfig<C, M, E>, M extends SimpleMetric, E extends SimpleEntity<?>>
        implements JacksonObject
{
    public static final long DEFALUT_WAIT_TIMEOUT = 100;

    public static final long DEFAULT_LOG_DURATION = 10 * 1000L;

    public long waitTimeout = DEFALUT_WAIT_TIMEOUT;

    // - public long queueLength = DEFALUT_WAIT_TIMEOUT;
    // - public long maxCapacity = DEFALUT_MAX_CAPACITY;
    // - public long pollTimeout = DEFALUT_WAIT_TIMEOUT;
    // - public long pushTimeout = DEFALUT_WAIT_TIMEOUT;

    public long logDuration = DEFAULT_LOG_DURATION;

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        JsonNode n;
        if ((n = node.get("wait_timeout")) != null) {
            this.waitTimeout = n.asLong(this.waitTimeout);
        }
        if ((n = node.get("log_duration")) != null) {
            this.logDuration = n.asLong(this.logDuration);
        }
    }

    public abstract SimpleAction<C, M, E> put(M metric, E entity);

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.set("wait_timeout", new LongNode(this.waitTimeout));
        node.set("log_duration", new LongNode(this.logDuration));
        return node;
    }
}
