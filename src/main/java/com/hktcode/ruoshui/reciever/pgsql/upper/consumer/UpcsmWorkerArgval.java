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
import com.hktcode.simple.SimpleWorkerArgval;

import java.util.concurrent.atomic.AtomicLong;

public class UpcsmWorkerArgval implements SimpleWorkerArgval
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
        actionInfosNode.set("items", UpcsmWkstepArgval.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpcsmWorkerArgval ofJsonObject(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode actionInfoNode = json.path("action_info");
        ArrayNode arrayNode;
        UpcsmWkstepArgval action;
        if (actionInfoNode instanceof MissingNode) {
            action = UpcsmWkstepArgval.ofJsonObject(MissingNode.getInstance());
        }
        else if ((arrayNode = (ArrayNode)actionInfoNode).size() == 0) {
            action = UpcsmWkstepArgval.ofJsonObject(MissingNode.getInstance());
        }
        else {
            action = UpcsmWkstepArgval.ofJsonObject(arrayNode.get(0));
        }
        return new UpcsmWorkerArgval(ImmutableList.of(action));
    }

    public final ImmutableList<UpcsmWkstepArgval> actionInfos;

    private UpcsmWorkerArgval(ImmutableList<UpcsmWkstepArgval> actionInfos)
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
        for (UpcsmWkstepArgval c: this.actionInfos) {
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
        return UpcsmWorker.of(this, UpcsmWorkerMeters.of(txactionLsn), exesvc);
    }
}