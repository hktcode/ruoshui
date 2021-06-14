/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.queue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class TqueueArgval implements JacksonObject
{
    public static final ObjectNode SCHEMA = JacksonObject.getFromResource(TqueueArgval.class, "TqueueConfig.yml");

    public static final long DEFALUT_WAIT_TIMEOUT = 100;

    public static final long DEFAULT_LOG_DURATION = 30 * 1000L;

    public static final int DEFALUT_MAX_CAPACITY = 1024;

    public static TqueueArgval ofJsonObject(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        TqueueArgval result = new TqueueArgval();
        result.waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        result.logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        result.maxCapacity = json.path("max_capacity").asInt(DEFALUT_MAX_CAPACITY);
        return result;
    }

    // - public long pollTimeout = DEFALUT_WAIT_TIMEOUT;
    // - public long pushTimeout = DEFALUT_WAIT_TIMEOUT;

    public long waitTimeout = DEFALUT_WAIT_TIMEOUT;

    public int maxCapacity = DEFALUT_MAX_CAPACITY;

    public long logDuration = DEFAULT_LOG_DURATION;

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.waitTimeout = node.path("wait_timeout").asLong(this.waitTimeout);
        this.logDuration = node.path("log_duration").asLong(this.logDuration);
        this.maxCapacity = node.path("max_capacity").asInt(this.maxCapacity);
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.set("max_capacity", new LongNode(this.maxCapacity));
        node.set("wait_timeout", new LongNode(this.waitTimeout));
        node.set("log_duration", new LongNode(this.logDuration));
        return node;
    }
}
