/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalReplArgval;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgConnectionProperty;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.*;

public class UpcsmWkstepArgval extends SimpleWkstepArgval
{
    public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UpcsmWkstepArgval.class, "UpcsmConfig.yml");

    public static UpcsmWkstepArgval ofJsonObject(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode srcPropertyNode = json.path("src_property");
        PgConnectionProperty srcProperty = PgConnectionProperty.ofJsonObject(srcPropertyNode);

        JsonNode logicalReplNode = json.path("logical_repl");
        LogicalReplArgval logicalRepl = LogicalReplArgval.of(logicalReplNode);

        UpcsmWkstepArgval result = new UpcsmWkstepArgval(srcProperty, logicalRepl);
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        result.waitTimeout = waitTimeout;
        result.logDuration = logDuration;
        return result;
    }

    public final PgConnectionProperty srcProperty;

    public final LogicalReplArgval logicalRepl;

    protected UpcsmWkstepArgval(PgConnectionProperty srcProperty, LogicalReplArgval logicalRepl)
    {
        this.srcProperty = srcProperty;
        this.logicalRepl = logicalRepl;
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

    public UpcsmWkstepAction action(Tqueue<UpperRecordConsumer> source)
    {
        if (source == null) {
            throw new ArgumentNullException("source");
        }
        return UpcsmWkstepAction.of(source);
    }
}
