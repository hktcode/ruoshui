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
            /* */, ObjectNode junction
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
        if (junction == null) {
            throw new ArgumentNullException("junction");
        }
        if (producer == null) {
            throw new ArgumentNullException("producer");
        }
        return new UpperResult(createts, fullname, consumer, junction, producer, deletets);
    }


    public final ObjectNode consumer;
    public final ObjectNode junction;
    public final ObjectNode producer;

    private UpperResult //
            /* */( long createts
            /* */, String fullname
            /* */, ObjectNode consumer
            /* */, ObjectNode junction
            /* */, ObjectNode producer
            /* */, long deletets
            /* */)
    {
        super(fullname, createts, deletets);
        this.consumer = consumer;
        this.junction = junction;
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
        ObjectNode junctionNode = node.putObject("junction");
        JacksonObject.copyTo(this.junction, junctionNode);
        ObjectNode producerNode = node.putObject("producer");
        JacksonObject.copyTo(this.producer, producerNode);
        node.put("deletets", this.deletets);
        return node;
    }
}
