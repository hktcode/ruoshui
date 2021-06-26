package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        ObjectNode actionInfosNode = argvalNode.putObject("actions_info");
        actionInfosNode.put("type", "array");
        actionInfosNode.set("items", UpcsmRecverArgval.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpcsmWorkerArgval of(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        Xqueue<UpperRecordConsumer> sender = Xqueue.of(json.path("sender"));
        UpcsmRecverArgval recver = UpcsmRecverArgval.of(json.path("recver"));
        UpcsmWorkerArgval result = new UpcsmWorkerArgval(recver, sender);
        result.xspins.pst(json.path("xspins"));
        return result;
    }

    public final Spins xspins = Spins.of();

    public final Xqueue<UpperRecordConsumer> sender;

    public final UpcsmRecverArgval recver;

    private UpcsmWorkerArgval(UpcsmRecverArgval recver, Xqueue<UpperRecordConsumer> sender)
    {
        this.sender = sender;
        this.recver = recver;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        return node;
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        // this.actionInfos.get(0).pst(node);
    }

    @Override
    public UpcsmWkstepAction action()
    {
        return UpcsmWkstepAction.of();
    }
}
