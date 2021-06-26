package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class UppdcSender
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        ObjectNode typeNode = schema.putObject("type");
        ArrayNode oneOfNode = typeNode.putArray("oneOf");
        oneOfNode.add(JacksonObject.getFromResource(UppdcSender.class, "UppdcSenderFiles.yml"));
        oneOfNode.add(JacksonObject.getFromResource(UppdcSender.class, "UppdcSenderKafka.yml"));
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UppdcSender of(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        String senderClass = json.path("sender_class").asText("files");
        UppdcSender result;
        if (senderClass.equals("kafka")) {
            result = UppdcSenderKafka.of(json);
        }
        else {
            result = UppdcSenderFiles.of(json);
        }
        return result;

    }

    public abstract Client client();

    // gauges
    public long offerTrycnt = 0;

    public long offerRowcnt = 0;

    public long offerCounts = 0;

    public final AtomicReference<Throwable> callbackRef = new AtomicReference<>();

    public final AtomicLong txactionLsn = new AtomicLong(0L);

    public interface Client extends AutoCloseable
    {
        void send(UpperRecordProducer record) throws Throwable;
    }

    protected UppdcSender()
    {
    }
}
