package com.hktcode.queue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.JsonResult;

import java.util.concurrent.atomic.AtomicReference;

public class XQueue<E>
{
    public static class Schema
    {
        public static final ObjectNode SCHEMA;

        // - $schema: http://json-schema.org/draft-04/schema#
        // - type: object
        // - properties:
        // -   max_messages: { type: integer, default: 1024, minimum: 0, maximum: 2147483647 }
        // -   min_messages: { type: integer, default: 1024, minimum: 0, maximum: 2147483647 }
        // -   max_duration: { type: integer, default: 1024, minimum: 0, maximum: 2147483647 }
        static
        {
            ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
            schema.put("$schema", "http://json-schema.org/draft-04/schema#");
            schema.put("type", "object");
            ObjectNode argvalNode = schema.putObject("properties");
            putInteger(argvalNode, "max_messages", DEFAULT_MAX_MESSAGES, 1, Integer.MAX_VALUE);
            putInteger(argvalNode, "min_messages", DEFAULT_MIN_MESSAGES, 1, Integer.MAX_VALUE);
            putInteger(argvalNode, "max_duration", DEFAULT_MAX_DURATION, 0, Long.MAX_VALUE);
            SCHEMA = JacksonObject.immutableCopy(schema);
        }

        private static void putInteger(ObjectNode node, String name, long defval, long minimum, long maximum)
        {
            ObjectNode result = node.putObject(name);
            result.put("type", "integer");
            result.put("default", defval);
            result.put("minimum", minimum);
            result.put("maximum", maximum);
        }
    }

    public static <E> XQueue<E> of(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        XQueue<E> result = new XQueue<>();
        result.pst(node);
        return result;
    }

    public static final int DEFAULT_MAX_MESSAGES = 1024;

    public static final int DEFAULT_MIN_MESSAGES = 16;

    public static final int DEFAULT_MAX_DURATION = 8;

    private XQueue()
    {
        this.atomicInner = new AtomicReference<>(XArray.of(0));
    }

    // - argval

    private int maxMessages = DEFAULT_MAX_MESSAGES;

    private int minMessages = DEFAULT_MIN_MESSAGES;

    private long maxDuration = DEFAULT_MAX_DURATION;

    // - gauges

    private final AtomicReference<XArray<E>> atomicInner;

    private long offerMillis = 0;

    private long offerTrycnt = 0;

    private long offerRowcnt = 0;

    private long offerCounts = 0;

    private long fetchTrycnt = 0;

    private long fetchRowcnt = 0;

    private long fetchCounts = 0;

    public XArray<E> push(XArray<E> lhs)
    {
        if (lhs == null) {
            throw new ArgumentNullException("lhs");
        }
        ++this.offerTrycnt;
        XArray<E> rhs = this.atomicInner.get();
        // 未来计划：支持bufferCount和maxDuration
        if (rhs.getSize() != 0) {
            return lhs;
        }
        this.offerRowcnt += lhs.getSize();
        this.atomicInner.set(lhs);
        ++this.offerCounts;
        rhs.setCapacity(this.maxMessages);
        return rhs;
    }

    public XArray<E> poll(XArray<E> rhs)
    {
        if (rhs == null) {
            throw new ArgumentNullException("rhs");
        }
        rhs.clear();
        ++this.fetchTrycnt;
        XArray<E> lhs = this.atomicInner.get();
        int size = lhs.getSize();
        if (size == 0) {
            return rhs;
        }
        this.fetchRowcnt += size;
        ++this.fetchCounts;
        this.atomicInner.set(rhs);
        return lhs;
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.maxMessages = node.path("max_messages").asInt(this.maxMessages);
        this.minMessages = node.path("min_messages").asInt(this.minMessages);
        this.maxDuration = node.path("max_duration").asLong(this.maxDuration);
        this.atomicInner.get().setCapacity(this.maxMessages);
    }

    public XArray<E> newArray()
    {
        return XArray.of(this.maxMessages);
    }

    public Result toJsonResult()
    {
        return new Result(new Config(this), new Metric(this));
    }

    public static class Result extends JsonResult<Config, Metric>
    {
        private Result(Config config, Metric metric)
        {
            super(config, metric);
        }
    }

    public static class Config implements JacksonObject
    {
        public final int maxMessages;

        public final int minMessages;

        public final long maxDuration;

        // - public final long minDuration;

        private <E> Config(XQueue<E> sender)
        {
            this.maxMessages = sender.maxMessages;
            this.minMessages = sender.minMessages;
            this.maxDuration = sender.maxDuration;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node.put("max_messages", this.maxMessages);
            node.put("min_messages", this.minMessages);
            node.put("max_duration", this.maxDuration);
            return node;
        }
    }

    public static class Metric implements JacksonObject
    {
        public final long fetchTrycnt;

        public final long fetchRowcnt;

        public final long fetchCounts;

        public final long offerTrycnt;

        public final long offerRowcnt;

        public final long offerCounts;

        public final long curMessages;

        public final long curCapacity;

        private <E> Metric(XQueue<E> sender)
        {
            this.fetchTrycnt = sender.fetchTrycnt;
            this.fetchRowcnt = sender.fetchRowcnt;
            this.fetchCounts = sender.fetchCounts;
            this.offerTrycnt = sender.offerTrycnt;
            this.offerRowcnt = sender.offerRowcnt;
            this.offerCounts = sender.offerCounts;
            XArray<E> list = sender.atomicInner.get();
            this.curCapacity = list.getCapacity();
            this.curMessages = list.getSize();
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
            node.put("fetch_trycnt", this.fetchTrycnt);
            node.put("fetch_rowcnt", this.fetchRowcnt);
            node.put("fetch_counts", this.fetchCounts);
            node.put("cur_capacity", this.curCapacity);
            node.put("cur_messages", this.curMessages);
            return node;
        }
    }
}
