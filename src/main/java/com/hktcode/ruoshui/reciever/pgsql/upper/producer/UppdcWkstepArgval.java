/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWkstepArgval;

import java.io.IOException;

public abstract class UppdcWkstepArgval extends SimpleWkstepArgval
{
    public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UppdcWkstepArgval.class, "UppdcArgval.yml");

    public static UppdcWkstepArgval ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        String factoryType = json.path("factory_type").asText("files");
        JsonNode senderPropsNode = json.path("sender_props");
        UppdcWkstepArgval result;
        if (factoryType.equals("kafka")) {
            result = UppdcWkstepArgvalKafka.ofJsonObject(senderPropsNode);
        }
        else {
            result = UppdcWkstepArgvalFiles.ofJsonObject(senderPropsNode);
        }
        result.waitTimeout = waitTimeout;
        result.logDuration = logDuration;
        return result;
    }

    public final String factoryType;

    protected UppdcWkstepArgval(String factoryType)
    {
        this.factoryType = factoryType;
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ObjectNode result = super.toJsonObject(node);
        result.put("factory_type", this.factoryType);
        result.put("wait_timeout", this.waitTimeout);
        result.put("log_duration", this.logDuration);
        return result;
    }

    public UppdcWkstepAction action()
    {
        return UppdcWkstepAction.of();
    }

    public abstract UppdcSender sender() throws IOException;
}
