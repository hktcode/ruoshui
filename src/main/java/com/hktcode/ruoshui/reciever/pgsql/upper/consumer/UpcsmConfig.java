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
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleConfig;

import java.util.concurrent.atomic.AtomicLong;

public class UpcsmConfig extends SimpleConfig
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

    public UpcsmWorker worker(AtomicLong txactionLsn, UpperExesvc exesvc)
    {
        if (txactionLsn == null) {
            throw new ArgumentNullException("txactionLsn");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        return UpcsmWorker.of(this, UpcsmMeters.of(txactionLsn), exesvc);
    }
}
