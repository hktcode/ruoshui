package com.hktcode.queue;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class OfferXqueue<E>
{
    private final AtomicReference<List<E>> atomicInner;

    // - argval

    private int maxMessages = 1024;

    private int minMessages = 16;

    private long maxDuration = 10;

    // - gauges

    private long offerTrycnt = 0;

    private long offerRowcnt = 0;

    private long offerCounts = 0;

    private OfferXqueue(AtomicReference<List<E>> atomicInner)
    {
        this.atomicInner = atomicInner;
    }

    public List<E> push(List<E> lhs)
    {
        if (lhs == null) {
            throw new ArgumentNullException("lhs");
        }
        ++this.offerTrycnt;
        List<E> rhs = this.atomicInner.get();
        if (!rhs.isEmpty()) {
            return lhs;
        }
        this.offerRowcnt += lhs.size();
        ++this.offerCounts;
        this.atomicInner.set(lhs);
        return rhs;
    }

    public Result toJsonResult()
    {
        return new Result(new Config(this), new Metric(this));
    }

    public static class Result implements JacksonObject
    {
        public final Config config;
        public final Metric metric;

        private Result(Config config, Metric metric)
        {
            this.config = config;
            this.metric = metric;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            ObjectNode configNode = node.putObject("config");
            this.config.toJsonObject(configNode);
            ObjectNode metricNode = node.putObject("metric");
            this.metric.toJsonObject(metricNode);
            return node;
        }
    }

    public static class Config implements JacksonObject
    {
        public final int maxMessages;

        public final int minMessages;

        public final long maxDuration;

        public final long minDuration;

        private <E> Config(OfferXqueue<E> sender)
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
        public final long offerTrycnt;
        // - public final long totalCounts;
        // - public final long successCnts;
        // - public final long MessageCnts;

        public final long offerRowcnt;

        public final long offerCounts;

        private <E> Metric(OfferXqueue<E> sender)
        {
            this.offerTrycnt = sender.offerTrycnt;
            this.offerRowcnt = sender.offerRowcnt;
            this.offerCounts = sender.offerCounts;
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
            return node;
        }
    }
}
