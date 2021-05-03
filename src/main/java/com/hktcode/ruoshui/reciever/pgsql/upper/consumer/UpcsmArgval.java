package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

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

public class UpcsmArgval implements SimpleArgval
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
        actionInfosNode.set("items", UpcsmConfig.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpcsmArgval ofJsonObject(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode actionInfoNode = json.path("action_info");
        ArrayNode arrayNode;
        UpcsmConfig action;
        if (actionInfoNode instanceof MissingNode) {
            action = UpcsmConfig.ofJsonObject(MissingNode.getInstance());
        }
        else if ((arrayNode = (ArrayNode)actionInfoNode).size() == 0) {
            action = UpcsmConfig.ofJsonObject(MissingNode.getInstance());
        }
        else {
            action = UpcsmConfig.ofJsonObject(arrayNode.get(0));
        }
        return new UpcsmArgval(ImmutableList.of(action));
    }

    public final ImmutableList<UpcsmConfig> actionInfos;

    private UpcsmArgval(ImmutableList<UpcsmConfig> actionInfos)
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
        for (UpcsmConfig c: this.actionInfos) {
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
