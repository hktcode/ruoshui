/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.simple.SimpleAction;
import com.hktcode.simple.SimpleConfig;

public abstract class UppdcConfig extends SimpleConfig<UppdcConfig, UppdcMetric, UpperHolder>
{
    public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UppdcConfig.class, "UppdcConfig.yml");

    public static UppdcConfig ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        String factoryType = json.path("factory_type").asText("files");
        JsonNode configPropsNode = json.path("config_props");
        UppdcConfig result;
        if (factoryType.equals("kafka")) {
            result = UppdcConfigKafka.ofJsonObject(configPropsNode);
        }
        else {
            result = UppdcConfigFiles.ofJsonObject(configPropsNode);
        }
        result.waitTimeout = waitTimeout;
        result.logDuration = logDuration;
        return result;
    }

    public final String factoryType;

    protected UppdcConfig(String factoryType)
    {
        this.factoryType = factoryType;
    }

    @Override
    public abstract UppdcActionRun put(UppdcMetric metric, UpperHolder entity);

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
}
