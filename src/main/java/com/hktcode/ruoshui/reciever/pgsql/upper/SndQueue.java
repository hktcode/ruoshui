package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SndQueue
{
    public static final class Schema
    {
        public static final ObjectNode SCHEMA;

        static
        {
            ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
            ObjectNode typeNode = schema.putObject("type");
            ArrayNode oneOfNode = typeNode.putArray("oneOf");
            oneOfNode.add(SndQueueFiles.Schema.SCHEMA);
            oneOfNode.add(SndQueueKafka.Schema.SCHEMA);
            SCHEMA = JacksonObject.immutableCopy(schema);
        }
    }

    public static SndQueue of(JsonNode json, AtomicLong xidlsn)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        String senderClass = json.path("sender_class").asText("files");
        SndQueue result;
        if ("kafka".equals(senderClass)) {
            result = SndQueueKafka.of(json, xidlsn);
        }
        else if ("files".equals(senderClass)) {
            result = SndQueueFiles.of(json, xidlsn);
        }
        else {
            throw new RuntimeException(); // 未来计划：自定义异常
        }
        return result;
    }

    public abstract Client client();

    public abstract Result toJsonResult();

    // gauges
    public long offerTrycnt = 0;

    public long offerRowcnt = 0;

    public long offerCounts = 0;

    public final AtomicReference<Throwable> callbackRef;

    public final AtomicLong lastConfirm;

    public interface Client extends AutoCloseable
    {
        void send(RhsQueue.Record record) throws Throwable;
    }

    public static class Result extends JsonResult<Config, Metric>
    {
        protected Result(Config config, Metric metric)
        {
            super(config, metric);
        }
    }

    public static abstract class Config implements JacksonObject
    {
        public final String senderClass;

        protected Config(String senderClass)
        {
            this.senderClass = senderClass;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node.put("sender_class", this.senderClass);
            return node;
        }
    }

    public static abstract class Metric implements JacksonObject
    {
        public final long offerTrycnt;

        public final long offerRowcnt;

        public final long offerCounts;

        public final long lastConfirm;

        protected Metric(SndQueue sender)
        {
            this.offerTrycnt = sender.offerTrycnt;
            this.offerRowcnt = sender.offerRowcnt;
            this.offerCounts = sender.offerCounts;
            this.lastConfirm = sender.lastConfirm.get();
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node.put("offer_trycnt", this.offerTrycnt);
            node.put("offer_rowcnt", this.offerRowcnt);
            node.put("offer_counts", this.offerCounts);
            node.put("last_confirm", this.lastConfirm);
            return node;
        }
    }

    protected SndQueue(AtomicLong xidlsn)
    {
        this.lastConfirm = xidlsn;
        this.callbackRef = new AtomicReference<>();
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
    }
}
