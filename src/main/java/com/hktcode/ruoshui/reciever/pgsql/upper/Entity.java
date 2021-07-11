package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
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
            propertiesNode.set("consumer", Consumer.Schema.SCHEMA);
            propertiesNode.set("lhsqueue", LhsQueue.Schema.SCHEMA);
            propertiesNode.set("junction", Junction.Schema.SCHEMA);
            propertiesNode.set("rhsqueue", RhsQueue.Schema.SCHEMA);
            propertiesNode.set("producer", Producer.Schema.SCHEMA);
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
    public final Consumer consumer;
    public final LhsQueue lhsQueue;
    public final Junction junction;
    public final RhsQueue rhsQueue;
    public final Producer producer;
    public final SndQueue sndQueue;
    private final SimpleAtomic xbarrier;

    private Entity(String fullname, JsonNode jsonnode)
    {
        AtomicLong xidlsn = new AtomicLong(0L);
        this.createts = System.currentTimeMillis();
        this.fullname = fullname;
        this.rcvQueue = RcvQueue.of(jsonnode.path("rcvqueue"), xidlsn);
        this.lhsQueue = LhsQueue.of(jsonnode.path("lhsqueue"));
        this.rhsQueue = RhsQueue.of(jsonnode.path("rhsqueue"));
        this.sndQueue = SndQueue.of(jsonnode.path("sndqueue"), xidlsn);
        this.xbarrier = SimpleAtomic.of();
        this.consumer = Consumer.of(rcvQueue, lhsQueue, xbarrier);
        this.junction = Junction.of(lhsQueue, rhsQueue, xbarrier);
        this.producer = Producer.of(rhsQueue, sndQueue, xbarrier);
        this.consumer.pst(jsonnode.path("consumer"));
        this.junction.pst(jsonnode.path("junction"));
        this.producer.pst(jsonnode.path("producer"));
    }

    public SimpleWorker consumer()
    {
        return this.consumer;
    }

    public SimpleWorker junction()
    {
        return this.junction;
    }

    public SimpleWorker producer()
    {
        return this.producer;
    }

    public Result modify(long finish, JsonNode newval, Keeper keeper) //
            throws InterruptedException
    {
        if (newval == null) {
            throw new ArgumentNullException("newval");
        }
        if (keeper == null) {
            throw new ArgumentNullException("keeper");
        }
        return xbarrier.call((d)->this.pst(d, finish, newval, keeper));
    }

    private Result pst(long delete, long finish, JsonNode newval, Keeper keeper)
    {
        this.rcvQueue.pst(newval.path("rcvqueue"));
        this.consumer.pst(newval.path("consumer"));
        this.lhsQueue.pst(newval.path("lhsqueue"));
        this.junction.pst(newval.path("junction"));
        this.rhsQueue.pst(newval.path("rhsqueue"));
        this.producer.pst(newval.path("producer"));
        this.sndQueue.pst(newval.path("sndqueue"));
        if (delete == Long.MAX_VALUE) {
            delete = finish;
        }
        Result result = new Result(this, delete);
        keeper.call(result);
        return result;
    }

    @FunctionalInterface
    public interface Keeper
    {
        void call(Entity.Result argval);
    }

    public static class Result extends SimpleResult
    {
        public final RcvQueue.Result rcvqueue;
        public final Consumer.Result consumer;
        public final LhsQueue.Result lhsqueue;
        public final Junction.Result junction;
        public final RhsQueue.Result rhsqueue;
        public final Producer.Result producer;
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
