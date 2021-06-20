package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWorkerArgval;

public class UpjctWorkerArgval implements SimpleWorkerArgval<UpjctWorkerArgval, UpjctWorkerGauges>
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
        actionInfosNode.set("items", UpjctWkstepArgval.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpjctWorkerArgval ofJsonObject(JsonNode json, Xqueue<UpperRecordConsumer> poll) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (poll == null) {
            throw new ArgumentNullException("poll");
        }
        JsonNode actionInfosNode = json.path("action_infos");
        int maxCapacity = json.path("max_capacity").asInt(Xqueue.MAX_CAPACITY);
        ArrayNode arrayNode;
        UpjctWkstepArgval action;
        if (actionInfosNode instanceof MissingNode) {
            action = UpjctWkstepArgval.ofJsonObject(MissingNode.getInstance());
        }
        else if ((arrayNode = (ArrayNode)actionInfosNode).size() == 0) {
            action = UpjctWkstepArgval.ofJsonObject(MissingNode.getInstance());
        }
        else {
            action = UpjctWkstepArgval.ofJsonObject(arrayNode.get(0));
        }
        Xqueue<UpperRecordProducer> push = Xqueue.of(maxCapacity);
        return new UpjctWorkerArgval(ImmutableList.of(action), poll, push);
    }

    public final ImmutableList<UpjctWkstepArgval> actionInfos;
    public final Xqueue<UpperRecordConsumer> fetchXqueue;
    public final Xqueue<UpperRecordProducer> offerXqueue;

    public final Xqueue.Spins spinsArgval = Xqueue.Spins.of();

    // wkstep
    // xqueue
    // xspins

    private UpjctWorkerArgval //
        /**/( ImmutableList<UpjctWkstepArgval> actionInfos //
            , Xqueue<UpperRecordConsumer> fetchXqueue //
            , Xqueue<UpperRecordProducer> offerXqueue //
    )
    {
        this.actionInfos = actionInfos;
        this.fetchXqueue = fetchXqueue;
        this.offerXqueue = offerXqueue;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ArrayNode actionInfosNode = node.putArray("action_infos");
        for (UpjctWkstepArgval c: this.actionInfos) {
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
    public UpjctWkstepAction action()
    {
        return this.actionInfos.get(0).action();
    }
}
