package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorker;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorker;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorker;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;

import java.util.concurrent.atomic.AtomicLong;

public class UpperHolder implements JacksonObject
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode propertiesNode = schema.putObject("properties");
        propertiesNode.set("consumer", UpcsmWorker.SCHEMA);
        propertiesNode.set("junction", UpjctWorker.SCHEMA);
        propertiesNode.set("producer", UppdcWorker.SCHEMA);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpperHolder of(String fullname, JsonNode jsonnode)
    {
        if (fullname == null) {
            throw new ArgumentNullException("fullname");
        }
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        AtomicLong xidlsn = new AtomicLong(0L);
        UpcsmWorker consumer = UpcsmWorker.of(jsonnode.path("consumer"), xidlsn);
        Xqueue<UpperRecordConsumer> fetchXqueue = consumer.sender;
        UpjctWorker junction = UpjctWorker.ofJsonObject(jsonnode.path("junction"), fetchXqueue);
        Xqueue<UpperRecordProducer> offerXqueue = junction.sender;
        UppdcWorker producer = UppdcWorker.ofJsonObject(jsonnode.path("producer"), offerXqueue, xidlsn);
        return new UpperHolder(fullname, consumer, junction, producer);
    }

    public final long createts;
    public final String fullname;
    public final UpcsmWorker consumer; // laborer
    public final UpjctWorker junction;
    public final UppdcWorker producer;
    private final SimpleAtomic atomic;

    private UpperHolder //
        /* */(String fullname //
            /* */, UpcsmWorker consumer //
            /* */, UpjctWorker junction //
            /* */, UppdcWorker producer //
            /* */)
    {
        this.createts = System.currentTimeMillis();
        this.fullname = fullname;
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
        this.atomic = SimpleAtomic.of();
    }

    public SimpleWorker<UpcsmWorker> consumer()
    {
        // - return SimpleWorker.of(this.argval.srcprops, this.argval.consumer, this.argval.srcqueue, this.atomic);
        return SimpleWorker.of(this.consumer, this.atomic);
    }

    public SimpleWorker<UpjctWorker> junction()
    {
        return SimpleWorker.of(this.junction, this.atomic);
    }

    public SimpleWorker<UppdcWorker> producer()
    {
        return SimpleWorker.of(this.producer, this.atomic);
    }

    public UpperResult modify(long finishts, JsonNode jsonnode, SimpleKeeper storeman)
            throws InterruptedException
    {
        if (jsonnode == null) {
            throw new ArgumentNullException("node");
        }
        if (storeman == null) {
            throw new ArgumentNullException("storeman");
        }
        return this.atomic.call((d)->this.modify(d, finishts, jsonnode, storeman));
    }

    private UpperResult modify(long deletets, long finishts, JsonNode jsonnode, SimpleKeeper storeman)
    {
        JsonNode n;
        if ((n = jsonnode.get("consumer")) != null) {
            this.consumer.pst(n);
        }
        if ((n = jsonnode.get("junction")) != null) {
            this.junction.pst(n);
        }
        if ((n = jsonnode.get("producer")) != null) {
            this.producer.pst(n);
        }
        if (deletets == Long.MAX_VALUE) {
            deletets = finishts;
        }
        storeman.call(this);
        long createts = this.createts;
        String fullname = this.fullname;
        ObjectNode consumer = this.consumer.toJsonObject();
        ObjectNode junction = this.junction.toJsonObject();
        ObjectNode producer = this.producer.toJsonObject();
        return UpperResult.of(createts, fullname, consumer, junction, producer, deletets);
    }

    @FunctionalInterface
    public interface SimpleKeeper
    {
        void call(UpperHolder argval);
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ObjectNode c = node.putObject("consumer");
        this.consumer.toJsonObject(c);
        ObjectNode j = node.putObject("junction");
        this.junction.toJsonObject(j);
        ObjectNode p = node.putObject("producer");
        this.producer.toJsonObject(p);
        return node;
    }
}
