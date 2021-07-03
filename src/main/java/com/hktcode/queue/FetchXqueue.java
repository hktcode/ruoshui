package com.hktcode.queue;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FetchXqueue<E>
{
    private final AtomicReference<List<E>> atomicInner;

    // - gauges

    private long fetchTrycnt = 0;

    private long fetchRowcnt = 0;

    private long fetchCounts = 0;

    private FetchXqueue(AtomicReference<List<E>> atomicInner)
    {
        this.atomicInner = atomicInner;
    }

    public List<E> poll(List<E> rhs)
    {
        if (rhs == null) {
            throw new ArgumentNullException("rhs");
        }
        ++this.fetchTrycnt;
        List<E> lhs = this.atomicInner.get();
        if (lhs.isEmpty()) {
            return rhs;
        }
        this.fetchRowcnt += lhs.size();
        ++this.fetchCounts;
        this.atomicInner.set(rhs);
        return lhs;
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
        private <E> Config(FetchXqueue<E> sender)
        {
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            return node;
        }
    }

    public static class Metric implements JacksonObject
    {
        public final long fetchTrycnt;
        // - public final long totalCounts;
        // - public final long successCnts;
        // - public final long MessageCnts;

        public final long fetchRowcnt;

        public final long fetchCounts;

        private <E> Metric(FetchXqueue<E> sender)
        {
            this.fetchTrycnt = sender.fetchTrycnt;
            this.fetchRowcnt = sender.fetchRowcnt;
            this.fetchCounts = sender.fetchCounts;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node.put("fetch_trycnt", this.fetchTrycnt);
            node.put("fetch_rowcnt", this.fetchRowcnt);
            node.put("fetch_counts", this.fetchCounts);
            return node;
        }
    }
}
