package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleArgval;

import java.util.concurrent.atomic.AtomicLong;

public class UppdcArgval implements SimpleArgval
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode argvalNode = schema.putObject("properties");
        ObjectNode actionInfosNode = argvalNode.putObject("action_infos");
        actionInfosNode.put("type", "array");
        actionInfosNode.set("items", UppdcConfig.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UppdcArgval ofJsonObject(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode actionInfoNode = json.path("action_info");
        ArrayNode arrayNode;
        UppdcConfig action;
        if (actionInfoNode instanceof MissingNode) {
            action = UppdcConfig.ofJsonObject(MissingNode.getInstance());
        }
        else if ((arrayNode = (ArrayNode)actionInfoNode).size() == 0) {
            action = UppdcConfig.ofJsonObject(MissingNode.getInstance());
        }
        else {
            action = UppdcConfig.ofJsonObject(arrayNode.get(0));
        }
        return new UppdcArgval(ImmutableList.of(action));
    }

    public final ImmutableList<UppdcConfig> actionInfos;

    private UppdcArgval(ImmutableList<UppdcConfig> actionInfos)
    {
        this.actionInfos = actionInfos;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ArrayNode actionInfosNode = node.putArray("action_infos");
        for (UppdcConfig c: this.actionInfos) {
            ObjectNode n = actionInfosNode.addObject();
            c.toJsonObject(n);
        }
        return node;
    }

    @Override
    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.actionInfos.get(0).pst(node);
    }

    public UppdcWorker worker(AtomicLong txactionLsn, UpperExesvc exesvc)
    {
        if (txactionLsn == null) {
            throw new ArgumentNullException("txactionLsn");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        return UppdcWorker.of(this, UppdcMeters.of(txactionLsn), exesvc);
    }
}
