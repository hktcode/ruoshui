package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.simple.SimpleResult;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpperResult extends SimpleResult
{
    public static UpperResult of(UpperHolder entities, long deletets)
    {
        if (entities == null) {
            throw new ArgumentNullException("entities");
        }
        long createts = entities.createts;
        String fullname = entities.fullname;
        ObjectNode consumer = entities.consumer.toJsonObject();
        ObjectNode srcqueue = entities.srcqueue.toJsonObject();
        ObjectNode junction = entities.junction.toJsonObject();
        ObjectNode tgtqueue = entities.tgtqueue.toJsonObject();
        ObjectNode producer = entities.producer.toJsonObject();
        return new UpperResult(createts, fullname, consumer, srcqueue, junction, tgtqueue, producer, deletets);
    }

    public final ObjectNode consumer;
    public final ObjectNode srcqueue;
    public final ObjectNode junction;
    public final ObjectNode tgtqueue;
    public final ObjectNode producer;

    private UpperResult //
            /* */( long createts
            /* */, String fullname
            /* */, ObjectNode consumer
            /* */, ObjectNode srcqueue
            /* */, ObjectNode junction
            /* */, ObjectNode tgtqueue
            /* */, ObjectNode producer
            /* */, long deletets
            /* */)
    {
        super(fullname, createts, deletets);
        this.consumer = consumer;
        this.srcqueue = srcqueue;
        this.junction = junction;
        this.tgtqueue = tgtqueue;
        this.producer = producer;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("createts", this.createts);
        ObjectNode consumerNode = node.putObject("consumer");
        JacksonObject.copyTo(this.consumer, consumerNode);
        ObjectNode srcqueueNode = node.putObject("srcqueue");
        JacksonObject.copyTo(this.srcqueue, srcqueueNode);
        ObjectNode junctionNode = node.putObject("junction");
        JacksonObject.copyTo(this.junction, junctionNode);
        ObjectNode tgtqueueNode = node.putObject("tgtqueue");
        JacksonObject.copyTo(this.tgtqueue, tgtqueueNode);
        ObjectNode producerNode = node.putObject("producer");
        JacksonObject.copyTo(this.producer, producerNode);
        node.put("deletets", this.deletets);
        return node;
    }
}
