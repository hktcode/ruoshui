package com.hktcode.queue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.JsonResult;

import java.util.concurrent.atomic.AtomicReference;

import static com.hktcode.jackson.JacksonObject.putInt4;
import static com.hktcode.jackson.JacksonObject.putInt8;
import static java.lang.System.currentTimeMillis;

public class XQueue<E>
{
    public static class Schema
    {
        public static final ObjectNode SCHEMA;

        static
        {
            ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
            schema.put("type", "object");
            ObjectNode props = schema.putObject("properties");
            putInt4(props, "max_messages", DEFAULT_MAX_MESSAGES, 1);
            putInt4(props, "min_messages", DEFAULT_MIN_MESSAGES, 1);
            putInt8(props, "max_duration", DEFAULT_MAX_DURATION, 0);
            ObjectNode setNullable = props.putObject("set_nullable");
            setNullable.put("type", "boolean");
            setNullable.put("default", DEFAULT_SET_NULLABLE);
            SCHEMA = JacksonObject.immutableCopy(schema);
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

    public static final int DEFAULT_MAX_MESSAGES = 1024 * 8;

    public static final int DEFAULT_MIN_MESSAGES = 16;

    public static final int DEFAULT_MAX_DURATION = 8;

    public static final boolean DEFAULT_SET_NULLABLE = false;

    protected XQueue()
    {
        this.atomicInner = new AtomicReference<>(this.newArray());
    }

    // - argval

    private int maxMessages = DEFAULT_MAX_MESSAGES;

    private int minMessages = DEFAULT_MIN_MESSAGES;

    private long maxDuration = DEFAULT_MAX_DURATION;

    private boolean setNullable = DEFAULT_SET_NULLABLE;

    // - gauges

    private final AtomicReference<XArray<E>> atomicInner;

    private long nextOfferms = 0;

    private long maxPushsize = 0;

    private long fullFailure = 0;

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
        int size = rhs.getSize();
        // 未来计划：支持bufferCount和maxDuration
        int pushsize = lhs.getSize();
        if (size != 0) {
            if (pushsize == lhs.getCapacity()) {
                ++this.fullFailure;
            }
            return lhs;
        }
        long now = currentTimeMillis();
        if (now < this.nextOfferms && size < minMessages) {
            return lhs;
        }
        if (pushsize > this.maxPushsize) {
            this.maxPushsize = pushsize;
        }
        this.offerRowcnt += pushsize;
        this.atomicInner.set(lhs);
        ++this.offerCounts;
        this.nextOfferms = now + this.maxDuration;
        rhs.setCapacity(this.maxMessages);
        return rhs;
    }

    public XArray<E> poll(XArray<E> rhs)
    {
        if (rhs == null) {
            throw new ArgumentNullException("rhs");
        }
        rhs.clear(this.setNullable);
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
        this.setNullable = node.path("set_nullable").asBoolean(this.setNullable);
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

        public final boolean setNullable;

        // - public final long minDuration;

        private <E> Config(XQueue<E> sender)
        {
            this.maxMessages = sender.maxMessages;
            this.minMessages = sender.minMessages;
            this.maxDuration = sender.maxDuration;
            this.setNullable = sender.setNullable;
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
            node.put("set_nullable", this.setNullable);
            return node;
        }
    }

    public static class Metric implements JacksonObject
    {
        public final long maxPushsize;

        public final long fetchTrycnt;

        public final long fetchRowcnt;

        public final long fetchCounts;

        public final long offerTrycnt;

        public final long offerRowcnt;

        public final long offerCounts;

        public final long curMessages;

        public final long curCapacity;

        public final long fullFailure;

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
            this.maxPushsize = sender.maxPushsize;
            this.fullFailure = sender.fullFailure;
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
            node.put("max_pushsize", this.maxPushsize);
            node.put("full_failure", this.fullFailure);
            return node;
        }
    }
}
