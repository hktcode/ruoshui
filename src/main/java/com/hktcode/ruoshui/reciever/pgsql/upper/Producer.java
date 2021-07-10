package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xspins;
import com.hktcode.queue.XArray;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class Producer extends SimpleWorker
{
    public static class Schema
    {
        public static final ObjectNode SCHEMA;

        static {
            ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
            schema.put("type", "object");
            ObjectNode propertiesNode = schema.putObject("properties");
            propertiesNode.set("spins_config", Xspins.Schema.SCHEMA);
            SCHEMA = JacksonObject.immutableCopy(schema);
        }
    }

    public static Producer of(RhsQueue recver, SndQueue sender, SimpleAtomic atomic)
    {
        if (recver == null) {
            throw new ArgumentNullException("recver");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        return new Producer(recver, sender, atomic);
    }

    public final SndQueue sender;

    public final RhsQueue recver;

    public final Xspins xspins = Xspins.of();

    private Producer(RhsQueue recver, SndQueue sender, SimpleAtomic atomic)
    {
        super(atomic);
        this.sender = sender;
        this.recver = recver;
    }

    @Override
    public void run(SimpleAtomic atomic) throws Throwable
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        XArray<RhsQueue.Record> lhs, rhs = recver.newArray();
        long now, prelog = System.currentTimeMillis(), spins = 0;
        Iterator<RhsQueue.Record> iter = rhs.iterator();
        try (SndQueue.Client client = this.sender.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                long l = this.xspins.logDuration;
                if (iter.hasNext()) {
                    // 未来计划：send方法支持数组，发送多个记录，提高性能
                    client.send(iter.next());
                } else if ((lhs = recver.poll(rhs)) != rhs) {
                    iter = (rhs = lhs).iterator();
                } else if (prelog + l >= (now = System.currentTimeMillis())) {
                    logger.info("write to logDuration={}", l);
                    prelog = now;
                } else {
                    this.xspins.spins(spins++);
                }
            }
        }
    }

    public Result toJsonResult()
    {
        Xspins.Result spinsResult = this.xspins.toJsonResult();
        return new Result(new Config(spinsResult), new Metric(spinsResult));
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.xspins.pst(node.path("spins_config"));
    }

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    public static class Result extends JsonResult<Config, Metric>
    {
        private Result(Config config, Metric metric)
        {
            super(config, metric);
        }
    }

    public static class Config implements JacksonObject
    {
        public final Xspins.Config spinsConfig;

        private Config(Xspins.Result spinsResult)
        {
            this.spinsConfig = spinsResult.config;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            this.spinsConfig.toJsonObject(node.putObject("spins_config"));
            return node;
        }
    }

    public static class Metric implements JacksonObject
    {
        public final Xspins.Metric spinsMetric;

        private Metric(Xspins.Result spinsResult)
        {
            this.spinsMetric = spinsResult.metric;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            this.spinsMetric.toJsonObject(node.putObject("spins_metric"));
            return node;
        }
    }
}
