package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalReplArgval;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgConnectionProperty;

public class UpcsmRecverArgval
{
    public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UpcsmRecverArgval.class, "UpcsmRecver.yml");

    public static UpcsmRecverArgval of(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        // - JsonNode srcPropertyNode = json.path("sender_class");
        JsonNode srcPropertyNode = json.path("src_property");
        PgConnectionProperty srcProperty = PgConnectionProperty.ofJsonObject(srcPropertyNode);

        JsonNode logicalReplNode = json.path("logical_repl");
        LogicalReplArgval logicalRepl = LogicalReplArgval.of(logicalReplNode);

        return new UpcsmRecverArgval(srcProperty, logicalRepl);
    }

    public final PgConnectionProperty srcProperty;

    public final LogicalReplArgval logicalRepl;

    private UpcsmRecverArgval(PgConnectionProperty srcProperty, LogicalReplArgval logicalRepl)
    {
        this.srcProperty = srcProperty;
        this.logicalRepl = logicalRepl;
    }

    // - @Override
    // - public ObjectNode toJsonObject(ObjectNode node)
    // - {
    // -     if (node == null) {
    // -         throw new ArgumentNullException("node");
    // -     }
    // -     node = super.toJsonObject(node);
    // -     ObjectNode logicalReplNode = node.putObject("logical_repl");
    // -     this.logicalRepl.toJsonObject(logicalReplNode);
    // -     ObjectNode srcPropertyNode = node.putObject("src_property");
    // -     this.srcProperty.toJsonObject(srcPropertyNode);
    // -     return node;
    // - }
}
