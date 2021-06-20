package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.queue.Xqueue.Spins;
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

    public static UpcsmWorkerArgval ofJsonObject(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        int maxCapacity = json.path("max_capacity").asInt(Xqueue.MAX_CAPACITY);
        Xqueue<UpperRecordConsumer> push = Xqueue.of(maxCapacity);

        Spins spins = Spins.of();
        spins.waitTimeout = json.path("wait_timeout").asLong(Spins.WAIT_TIMEOUT);
        spins.spinsMaxcnt = json.path("spins_maxcnt").asLong(Spins.SPINS_MAXCNT);

        JsonNode actionInfosNode = json.path("action_infos");
        ArrayNode arrayNode;
        UpcsmWkstepArgval action;
        if (actionInfosNode instanceof MissingNode) {
            action = UpcsmWkstepArgval.ofJsonObject(MissingNode.getInstance());
        }
        else if ((arrayNode = (ArrayNode)actionInfosNode).size() == 0) {
            action = UpcsmWkstepArgval.ofJsonObject(MissingNode.getInstance());
        }
        else {
            action = UpcsmWkstepArgval.ofJsonObject(arrayNode.get(0));
        }
        return new UpcsmWorkerArgval(ImmutableList.of(action), push, spins);
    }

    public final ImmutableList<UpcsmWkstepArgval> actionInfos;

    public final Xqueue<UpperRecordConsumer> offerXqueue;

    public final Spins spinsArgval;

    public long logDuration = SimpleWorkerArgval.LOG_DURATION;
    // - public final PgConnectionProperty srcProperty;
    // - public final LogicalReplArgval logicalRepl;
    // - public int bufferCount;

    private UpcsmWorkerArgval //
        /**/( ImmutableList<UpcsmWkstepArgval> actionInfos //
            , Xqueue<UpperRecordConsumer> offerXqueue //
            , Spins spinsArgval //
    )
    {
        this.actionInfos = actionInfos;
        this.offerXqueue = offerXqueue;
        this.spinsArgval = spinsArgval;
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
        return this.actionInfos.get(0).action();
    }
}
