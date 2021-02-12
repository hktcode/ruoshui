package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.simple.SimpleResult;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpperResult extends SimpleResult
{
    public static UpperResult of //
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
        if (fullname == null) {
            throw new ArgumentNullException("fullname");
        }
        if (consumer == null) {
            throw new ArgumentNullException("consumer");
        }
        if (srcqueue == null) {
            throw new ArgumentNullException("srcqueue");
        }
        if (junction == null) {
            throw new ArgumentNullException("junction");
        }
        if (tgtqueue == null) {
            throw new ArgumentNullException("tgtqueue");
        }
        if (producer == null) {
            throw new ArgumentNullException("producer");
        }
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
        node.put("fullname", this.fullname);
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
