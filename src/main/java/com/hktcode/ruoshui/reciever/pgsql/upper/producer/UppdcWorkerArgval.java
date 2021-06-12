package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWorkerArgval;

public class UppdcWorkerArgval implements SimpleWorkerArgval<UppdcWorkerArgval, UppdcWorkerMeters>
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
        actionInfosNode.set("items", UppdcWkstepArgval.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UppdcWorkerArgval ofJsonObject(JsonNode json, Tqueue<UpperRecordProducer> poll)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (poll == null) {
            throw new ArgumentNullException("poll");
        }
        JsonNode actionInfoNode = json.path("action_infos");
        JsonNode actionNode = actionInfoNode.path(0);
        UppdcWkstepArgval action = UppdcWkstepArgval.ofJsonObject(actionNode);
        return new UppdcWorkerArgval(ImmutableList.of(action), poll);
    }

    public final ImmutableList<UppdcWkstepArgval> actionInfos;

    private final Tqueue<UpperRecordProducer> fetchTqueue;

    private UppdcWorkerArgval(ImmutableList<UppdcWkstepArgval> actionInfos, Tqueue<UpperRecordProducer> fetchTqueue)
    {
        this.actionInfos = actionInfos;
        this.fetchTqueue = fetchTqueue;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ArrayNode actionInfosNode = node.putArray("action_infos");
        for (UppdcWkstepArgval c: this.actionInfos) {
            ObjectNode n = actionInfosNode.addObject();
            c.toJsonObject(n);
        }
        return node;
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.actionInfos.get(0).pst(node);
    }

    @Override
    public UppdcWkstepAction action()
    {
        return this.actionInfos.get(0).action(this.fetchTqueue);
    }
}
