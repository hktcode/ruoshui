package com.hktcode.queue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.JsonResult;

import static com.hktcode.jackson.JacksonObject.putInteger;

public class Xspins
{
    public static class Schema
    {
        public static final ObjectNode SCHEMA;

        static
        {
            ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
            schema.put("$schema", "http://json-schema.org/draft-04/schema#");
            schema.put("type", "object");
            ObjectNode argvalNode = schema.putObject("properties");
            putInteger(argvalNode, "wait_timeout", DEFAULT_WAIT_TIMEOUT, 0, Integer.MAX_VALUE);
            putInteger(argvalNode, "spins_maxcnt", DEFAULT_SPINS_MAXCNT, 0, Integer.MAX_VALUE);
            putInteger(argvalNode, "log_duration", DEFAULT_LOG_DURATION, 0, Long.MAX_VALUE);
            SCHEMA = JacksonObject.immutableCopy(schema);
        }
    }

    public static final long DEFAULT_WAIT_TIMEOUT = 128;

    public static final long DEFAULT_SPINS_MAXCNT = 1024;

    public static final long DEFAULT_LOG_DURATION = 5 * 60 * 1000;

    public static Xspins of()
    {
        return new Xspins();
    }

    public static final int RESET = 0;
    public static final int SPINS = 1;
    public static final int YIELD = 2;
    public static final int SLEEP = 3;

    // config
    public long waitTimeout = DEFAULT_WAIT_TIMEOUT;
    public long spinsMaxcnt = DEFAULT_SPINS_MAXCNT;
    public long logDuration = DEFAULT_LOG_DURATION;

    // gauges
    public long spinsCounts = 0;
    public long yieldCounts = 0;
    public long sleepCounts = 0;
    public long yieldMillis = 0;
    public long sleepMillis = 0;
    public long spinsStarts = 0;

    private Xspins()
    {
    }

    public void pst(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        waitTimeout = json.path("wait_timeout").asLong(waitTimeout);
        spinsMaxcnt = json.path("spins_maxcnt").asLong(spinsMaxcnt);
        logDuration = json.path("log_duration").asLong(logDuration);
    }

    public int spins(long spins) throws InterruptedException
    {
        if (spins > spinsMaxcnt) {
            long starts = System.currentTimeMillis();
            ++sleepCounts;
            long duration = starts - spinsStarts;
            long millis = this.waitTimeout - duration;
            millis = millis > 0 ? millis : 0;
            Thread.sleep(millis);
            long finish = System.currentTimeMillis();
            sleepMillis += finish - starts;
            return RESET;
        } else if ((spinsCounts + yieldCounts) % 2 == 0) {
            ++spinsCounts;
            spinsStarts = spins == 0 ? System.currentTimeMillis() : spinsStarts;
            return spins == spinsMaxcnt ? SLEEP : YIELD;
        } else {
            long starts = System.currentTimeMillis();
            ++yieldCounts;
            Thread.yield();
            long finish = System.currentTimeMillis();
            yieldMillis += finish + starts;
            return spins == spinsMaxcnt ? SLEEP : SPINS;
        }
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
        public final long waitTimeout;
        public final long spinsMaxcnt;
        public final long logDuration;

        private Config(Xspins xspins)
        {
            this.waitTimeout = xspins.waitTimeout;
            this.spinsMaxcnt = xspins.spinsMaxcnt;
            this.logDuration = xspins.logDuration;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node.put("wait_timeout", this.waitTimeout);
            node.put("spins_maxcnt", this.spinsMaxcnt);
            node.put("log_duration", this.logDuration);
            return node;
        }
    }

    public static class Metric implements JacksonObject
    {
        public final long spinsCounts;
        public final long yieldCounts;
        public final long sleepCounts;
        public final long yieldMillis;
        public final long sleepMillis;

        private Metric(Xspins xspins)
        {
            this.spinsCounts = xspins.spinsCounts;
            this.yieldCounts = xspins.yieldCounts;
            this.sleepCounts = xspins.sleepCounts;
            this.yieldMillis = xspins.yieldMillis;
            this.sleepMillis = xspins.sleepCounts;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node.put("spins_counts", this.spinsCounts);
            node.put("yield_counts", this.yieldCounts);
            node.put("sleep_counts", this.sleepCounts);
            node.put("yield_millis", this.yieldMillis);
            node.put("sleep_millis", this.sleepMillis);
            return node;
        }
    }
}
