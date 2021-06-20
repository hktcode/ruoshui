package com.hktcode.queue;

import com.hktcode.lang.exception.ArgumentNegativeException;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Xqueue<E>
{
    public static <E> Xqueue<E> of(int maxCapacity)
    {
        if (maxCapacity <= 0) {
            throw new ArgumentNegativeException("maxCapacity", maxCapacity);
        }
        return new Xqueue<>(maxCapacity);
    }

    public static final int MAX_CAPACITY = 1024;

    private final AtomicReference<List<E>> atomic;

    public int maxCapacity;

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

    private static abstract class Queue<E>
    {
        public final Xqueue<E> xqueue;

        protected Queue(Xqueue<E> xqueue)
        {
            this.xqueue = xqueue;
        }

        public List<E> list()
        {
            return new ArrayList<>(xqueue.maxCapacity);
        }
    }

    public static class Offer<E> extends Queue<E>
    {
        public long trycnt = 0;
        public long bufcnt = 0;
        public long rowcnt = 0;

        private Offer(Xqueue<E> xqueue)
        {
            super(xqueue);
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

    public static class Fetch<E> extends Queue<E>
    {
        public long trycnt = 0;
        public long bufcnt = 0;
        public long rowcnt = 0;

        private Fetch(Xqueue<E> xqueue)
        {
            super(xqueue);
        }

        public List<E> poll(List<E> rhs)
        {
            if (rhs == null) {
                throw new ArgumentNullException("rhs");
            }
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

    public static class Spins
    {
        public static final long WAIT_TIMEOUT = 128;

        public static final long SPINS_MAXCNT = 1024;

        public static Spins of()
        {
            return new Spins();
        }

        public static final int RESET = 0;
        public static final int SPINS = 1;
        public static final int YIELD = 2;
        public static final int SLEEP = 3;

        // config
        public long waitTimeout = 128;
        public long spinsMaxcnt = 1024;
        // - public long logDuration;

        // gauges
        public long spinsCounts = 0;
        public long yieldCounts = 0;
        public long sleepCounts = 0;
        public long sleepMillis = 0;
        public long spinsStarts = 0;

        private Spins()
        {
        }

        public int spins(long spins) throws InterruptedException
        {
            if (spins > spinsMaxcnt) {
                ++sleepCounts;
                long duration = System.currentTimeMillis() - spinsStarts;
                long millis = this.waitTimeout - duration;
                millis = millis > 0 ? millis : 0;
                Thread.sleep(millis);
                sleepMillis += millis;
                return RESET;
            }
            if ((spinsCounts + yieldCounts) % 2 == 0) {
                ++spinsCounts;
                spinsStarts = spins == 0 ? System.currentTimeMillis() : spinsStarts;
                return spins == spinsMaxcnt ? SLEEP : YIELD;
            }
            else {
                ++yieldCounts;
                Thread.yield();
                return spins == spinsMaxcnt ? SLEEP : SPINS;
            }
        }
    }
}