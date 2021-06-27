package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWorkerArgval;
import com.hktcode.simple.SimpleWorkerGauges;

import java.util.concurrent.atomic.AtomicLong;

public class UppdcWorkerArgval extends SimpleWorkerGauges //
        implements SimpleWorkerArgval<UppdcWorkerArgval, UppdcWorkerArgval>
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
        actionInfosNode.set("items", UppdcSender.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UppdcWorkerArgval ofJsonObject(JsonNode json, Xqueue<UpperRecordProducer> recver, AtomicLong xidlsn)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (recver == null) {
            throw new ArgumentNullException("recver");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        UppdcSender sender = UppdcSender.of(json.path("sender"), xidlsn);
        UppdcWorkerArgval result = new UppdcWorkerArgval(sender, recver);
        result.xspins.pst(json.path("xspins"));
        return result;
    }

    public final UppdcSender sender;

    public final Xqueue<UpperRecordProducer> recver;

    public final Xqueue.Spins xspins = Xqueue.Spins.of();

    private UppdcWorkerArgval(UppdcSender sender, Xqueue<UpperRecordProducer> recver)
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
    }

    @Override
    public UppdcWkstepAction action()
    {
        return UppdcWkstepAction.of();
    }
}
