package com.hktcode.queue;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Xqueue<E>
{
    public static <E> Xqueue<E> of(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        return new Xqueue<>(json.asInt(MAX_CAPACITY));
    }

    public static final int MAX_CAPACITY = 1024;

    private final AtomicReference<List<E>> atomic; // TODO: 采用数组，而不是List，自定义此对象

    public int maxCapacity;

    // - argval
    // -
    // - public long minMessages;
    // - public long maxMessages;

    // - gauges
    // -
    // - public long fetchTrycnt;
    // - public long fetchSuccnt;
    // - public long fetchCounts;

    // - public long offerTrycnt;
    // - public long offerSuccnt;
    // - public long offerCounts;

    public static class XList<E>
    {
        public final E[] list;

        public int size = 0;

        private XList(E[] list)
        {
            this.list = list;
        }
    }

    private Xqueue(int maxCapacity)
    {
        this.maxCapacity = maxCapacity;
        this.atomic = new AtomicReference<>(new ArrayList<>(maxCapacity));
    }

    public Offer<E> offerXqueue()
    {
        return new Offer<>(this);
    }

    public Fetch<E> fetchXqueue()
    {
        return new Fetch<>(this);
    }

    public List<E> list()
    {
        return new ArrayList<>(this.maxCapacity);
    }

    public static class Offer<E>
    {
        private final Xqueue<E> xqueue;

        public long trycnt = 0;
        public long bufcnt = 0;
        public long rowcnt = 0;

        private Offer(Xqueue<E> xqueue)
        {
            this.xqueue = xqueue;
        }

        public List<E> push(List<E> lhs)
        {
            if (lhs == null) {
                throw new ArgumentNullException("lhs");
            }
            ++this.trycnt;
            List<E> rhs = this.xqueue.atomic.get();
            if (!rhs.isEmpty()) {
                return lhs;
            }
            this.rowcnt += lhs.size();
            this.xqueue.atomic.set(lhs);
            ++this.bufcnt;
            return rhs;
        }
    }

    public static class Fetch<E>
    {
        private final Xqueue<E> xqueue;

        public long trycnt = 0;
        public long bufcnt = 0;
        public long rowcnt = 0;

        private Fetch(Xqueue<E> xqueue)
        {
            this.xqueue = xqueue;
        }

        public List<E> poll(List<E> rhs)
        {
            if (rhs == null) {
                throw new ArgumentNullException("rhs");
            }
            rhs.clear();
            ++this.trycnt;
            List<E> lhs = this.xqueue.atomic.get();
            if (lhs.isEmpty()) {
                return rhs;
            }
            this.rowcnt += lhs.size();
            this.xqueue.atomic.set(rhs);
            ++this.bufcnt;
            return lhs;
        }

        // - public String toStringText();
        // - public String getConfigObj();
        // - public String getMetricObj();
    }
}
