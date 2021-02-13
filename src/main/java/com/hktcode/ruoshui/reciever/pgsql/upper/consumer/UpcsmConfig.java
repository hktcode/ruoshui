/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalReplConfig;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgConnectionProperty;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.simple.SimpleConfig;

public class UpcsmConfig extends SimpleConfig<UpcsmConfig, UpcsmMetric, UpperHolder>
{
    public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UpcsmConfig.class, "UpcsmConfig.yml");

    public static UpcsmConfig ofJsonObject(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode srcPropertyNode = json.path("src_property");
        PgConnectionProperty srcProperty = PgConnectionProperty.ofJsonObject(srcPropertyNode);

        JsonNode logicalReplNode = json.path("logical_repl");
        LogicalReplConfig logicalRepl = LogicalReplConfig.of(logicalReplNode);

        UpcsmConfig result = new UpcsmConfig(srcProperty, logicalRepl);
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        result.waitTimeout = waitTimeout;
        result.logDuration = logDuration;
        return result;
    }

    public final PgConnectionProperty srcProperty;

    public final LogicalReplConfig logicalRepl;

    protected UpcsmConfig(PgConnectionProperty srcProperty, LogicalReplConfig logicalRepl)
    {
        this.srcProperty = srcProperty;
        this.logicalRepl = logicalRepl;
    }

    @Override
    public UpcsmActionRun put(UpcsmMetric metric, UpperHolder entity)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (entity == null) {
            throw new ArgumentNullException("entity");
        }
        return UpcsmActionRun.of(this, metric, entity);
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node = super.toJsonObject(node);
        ObjectNode logicalReplNode = node.putObject("logical_repl");
        this.logicalRepl.toJsonObject(logicalReplNode);
        ObjectNode srcPropertyNode = node.putObject("src_property");
        this.srcProperty.toJsonObject(srcPropertyNode);
        return node;
    }
}
