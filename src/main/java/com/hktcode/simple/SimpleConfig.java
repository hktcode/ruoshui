/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleConfig implements JacksonObject
{
    public static final long DEFALUT_WAIT_TIMEOUT = 100;

    public static final long DEFAULT_LOG_DURATION = 10 * 1000L;

    public static SimpleConfig ofJsonObject(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        SimpleConfig result = new SimpleConfig();
        result.waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        result.logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        return result;
    }

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
