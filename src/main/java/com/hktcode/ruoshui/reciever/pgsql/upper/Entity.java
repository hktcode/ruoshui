package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.XQueue;
import com.hktcode.queue.Xspins;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleResult;
import com.hktcode.simple.SimpleWorker;

import java.util.concurrent.atomic.AtomicLong;

public class Entity
{
    public static class Schema
    {
        public static final ObjectNode SCHEMA;

        static {
            ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
            schema.put("$schema", "http://json-schema.org/draft-04/schema#");
            schema.put("type", "object");
            ObjectNode propertiesNode = schema.putObject("properties");
            propertiesNode.set("rcvqueue", RcvQueue.Schema.SCHEMA);
            propertiesNode.set("consumer", Xspins.Schema.SCHEMA);
            propertiesNode.set("lhsqueue", XQueue.Schema.SCHEMA);
            propertiesNode.set("junction", Xspins.Schema.SCHEMA);
            propertiesNode.set("rhsqueue", XQueue.Schema.SCHEMA);
            propertiesNode.set("producer", Xspins.Schema.SCHEMA);
            propertiesNode.set("sndqueue", SndQueue.Schema.SCHEMA);
            SCHEMA = JacksonObject.immutableCopy(schema);
        }
    }

    public static Entity of(String fullname, JsonNode jsonnode)
    {
        if (fullname == null) {
            throw new ArgumentNullException("fullname");
        }
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        return new Entity(fullname, jsonnode);
    }

    public final long createts;
    public final String fullname;
    public final RcvQueue rcvQueue;
    public final Xspins consumer;
    public final LhsQueue lhsQueue;
    public final Xspins junction;
    public final RhsQueue rhsQueue;
    public final Xspins producer;
    public final SndQueue sndQueue;
    private final SimpleAtomic xbarrier;

    private Entity(String fullname, JsonNode jsonnode)
    {
        AtomicLong xidlsn = new AtomicLong(0L);
        this.createts = System.currentTimeMillis();
        this.fullname = fullname;
        this.rcvQueue = RcvQueue.of(jsonnode.path("rcvqueue"), xidlsn);
        this.consumer = Xspins.of();
        this.lhsQueue = LhsQueue.of(jsonnode.path("lhsqueue"));
        this.junction = Xspins.of();
        this.rhsQueue = RhsQueue.of(jsonnode.path("rhsqueue"));
        this.producer = Xspins.of();
        this.sndQueue = SndQueue.of(jsonnode.path("sndqueue"), xidlsn);
        this.xbarrier = SimpleAtomic.of();
        this.consumer.pst(jsonnode.path("consumer"));
        this.junction.pst(jsonnode.path("junction"));
        this.producer.pst(jsonnode.path("producer"));
    }

    public SimpleWorker consumer()
    {
        return Consumer.of(rcvQueue, lhsQueue, consumer, xbarrier);
    }

    public SimpleWorker junction()
    {
        return Junction.of(lhsQueue, rhsQueue, junction, xbarrier);
    }

    public SimpleWorker producer()
    {
        return Producer.of(rhsQueue, sndQueue, producer, xbarrier);
    }

    public Result modify(long finishts, JsonNode jsonnode, SimpleKeeper storeman)
            throws InterruptedException
    {
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        if (storeman == null) {
            throw new ArgumentNullException("storeman");
        }
        return xbarrier.call((d)->this.modify(d, finishts, jsonnode, storeman));
    }

    private Result modify(long deletets, long finishts, JsonNode jsonnode, SimpleKeeper storeman)
    {
        this.rcvQueue.pst(jsonnode.path("rcvqueue"));
        this.consumer.pst(jsonnode.path("consumer"));
        this.lhsQueue.pst(jsonnode.path("lhsqueue"));
        this.junction.pst(jsonnode.path("junction"));
        this.rhsQueue.pst(jsonnode.path("rhsqueue"));
        this.producer.pst(jsonnode.path("producer"));
        this.sndQueue.pst(jsonnode.path("sndqueue"));
        if (deletets == Long.MAX_VALUE) {
            deletets = finishts;
        }
        Result result = new Result(this, deletets);
        storeman.call(result);
        return result;
    }

    @FunctionalInterface
    public interface SimpleKeeper
    {
        void call(Entity.Result argval);
    }

    public static class Result extends SimpleResult
    {
        public final RcvQueue.Result rcvqueue;
        public final Xspins.Result consumer;
        public final XQueue.Result lhsqueue;
        public final Xspins.Result junction;
        public final XQueue.Result rhsqueue;
        public final Xspins.Result producer;
        public final SndQueue.Result sndqueue;

        private Result(Entity holder, long deletets)
        {
            super(holder.fullname, holder.createts, deletets);
            this.rcvqueue = holder.rcvQueue.toJsonResult();
            this.consumer = holder.consumer.toJsonResult();
            this.lhsqueue = holder.lhsQueue.toJsonResult();
            this.junction = holder.junction.toJsonResult();
            this.rhsqueue = holder.rhsQueue.toJsonResult();
            this.producer = holder.producer.toJsonResult();
            this.sndqueue = holder.sndQueue.toJsonResult();
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node = super.toJsonObject(node);
            this.rcvqueue.toJsonObject(node.putObject("rcvqueue"));
            this.consumer.toJsonObject(node.putObject("consumer"));
            this.lhsqueue.toJsonObject(node.putObject("lhsqueue"));
            this.junction.toJsonObject(node.putObject("junction"));
            this.rhsqueue.toJsonObject(node.putObject("rhsqueue"));
            this.producer.toJsonObject(node.putObject("producer"));
            this.sndqueue.toJsonObject(node.putObject("sndqueue"));
            return node;
        }
    }
}
