package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.XQueue;
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
        propertiesNode.set("consumer", Consumer.SCHEMA);
        propertiesNode.set("junction", Junction.SCHEMA);
        propertiesNode.set("producer", Producer.SCHEMA);
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
        SimpleAtomic atomic = SimpleAtomic.of();
        AtomicLong xidlsn = new AtomicLong(0L);
        Consumer consumer = Consumer.of(jsonnode.path("consumer"), xidlsn, atomic);
        XQueue<UpperRecordConsumer> fetchXqueue = consumer.sender;
        Junction junction = Junction.ofJsonObject(jsonnode.path("junction"), fetchXqueue, atomic);
        XQueue<UpperRecordProducer> offerXqueue = junction.sender;
        Producer producer = Producer.ofJsonObject(jsonnode.path("producer"), offerXqueue, xidlsn, atomic);
        return new UpperHolder(fullname, consumer, junction, producer, atomic);
    }

    public final long createts;
    public final String fullname;
    // fromdest
    //   sender_props
    //   recver_props
    //   txaction_lsn
    public final Consumer consumer; // laborer
    // srcqueue
    public final Junction junction;
    // tgtqueue
    public final Producer producer;
    private final SimpleAtomic atomic; // xbarrier

    private UpperHolder //
        /* */(String fullname //
            /* */, Consumer consumer //
            /* */, Junction junction //
            /* */, Producer producer //
            /* */, SimpleAtomic atomic
            /* */)
    {
        this.createts = System.currentTimeMillis();
        this.fullname = fullname;
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
        this.atomic = atomic;
    }

    public SimpleWorker consumer()
    {
        // - return UpcsmWorker.of(srcprops, consumer, srcqueue, xbarrier);
        return this.consumer;
    }

    public SimpleWorker junction()
    {
        // - return SimpleWorker.of(srcqueue, tgtqueue, xbarrier);
        return this.junction;
    }

    public SimpleWorker producer()
    {
        // - return SimpleWorker.of(tgtqueue, tgtprops, xbarrier);
        return this.producer;
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
