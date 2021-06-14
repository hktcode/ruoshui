package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.SimpleWorkerArgval;

public class UpcsmWorkerArgval implements SimpleWorkerArgval<UpcsmWorkerArgval, UpcsmWorkerGauges>
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

    public static UpcsmWorkerArgval ofJsonObject(JsonNode json, Tqueue<UpperRecordConsumer> push) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (push == null) {
            throw new ArgumentNullException("push");
        }
        JsonNode actionInfoNode = json.path("action_infos");
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
        return new UpcsmWorkerArgval(ImmutableList.of(action), push);
    }

    public final ImmutableList<UpcsmWkstepArgval> actionInfos;

    private final Tqueue<UpperRecordConsumer> offerTqueue;

    private UpcsmWorkerArgval(ImmutableList<UpcsmWkstepArgval> actionInfos, Tqueue<UpperRecordConsumer> offerTqueue)
    {
        this.actionInfos = actionInfos;
        this.offerTqueue = offerTqueue;
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

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.actionInfos.get(0).pst(node);
    }

    @Override
    public UpcsmWkstepAction action()
    {
        return this.actionInfos.get(0).action(this.offerTqueue);
    }
}
