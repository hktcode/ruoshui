package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.XArray;
import com.hktcode.queue.Xspins;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static java.lang.System.currentTimeMillis;

public class Consumer extends SimpleWorker
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

    public static Consumer //
    of(RcvQueue recver, LhsQueue sender, SimpleAtomic atomic)
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
        return new Consumer(recver, sender, atomic);
    }

    private final Xspins xspins = Xspins.of();

    private final LhsQueue sender;

    private final RcvQueue recver;

    @Override
    protected void run(SimpleAtomic atomic) //
            throws SQLException, InterruptedException
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        LhsQueue.Record r = null;
        XArray<LhsQueue.Record> rhs, lhs = this.sender.newArray();
        int spins = 0, spinsStatus = Xspins.RESET;
        long now, logms = currentTimeMillis();
        try (RcvQueue.Client client = this.recver.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                // 未来计划：此处可以提高性能
                long logDuration = this.xspins.logDuration;
                if (lhs.getSize() > 0 && (rhs = sender.push(lhs)) != lhs) {
                    lhs = rhs;
                    spins = 0;
                    logms = currentTimeMillis();
                } else if (r == null) {
                    r = client.recv();
                } else if (lhs.add(r)) {
                    spins = 0;
                    logms = currentTimeMillis();
                } else if (logms + logDuration >= (now = currentTimeMillis())) {
                    logger.info("logDuration={}", logDuration);
                    logms = now;
                } else {
                    if (spinsStatus == Xspins.SLEEP) {
                        client.forceUpdateStatus();
                    }
                    spinsStatus = this.xspins.spins(spins++);
                }
            }
        }
        logger.info("pgsender complete");
    }

    public Result toJsonResult()
    {
        Xspins.Result spinsResult = this.xspins.toJsonResult();
        Config config = new Config(spinsResult);
        Metric metric = new Metric(spinsResult, this);
        return new Result(config, metric);
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.xspins.pst(node.path("spins_config"));
    }

    private Consumer(RcvQueue recver, LhsQueue sender, SimpleAtomic atomic)
    {
        super(atomic);
        this.sender = sender;
        this.recver = recver;
    }

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

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

    public static class Metric extends SimpleWorker.Metric
    {
        public final Xspins.Metric spinsMetric;

        private Metric(Xspins.Result spinsResult, Consumer consumer)
        {
            super(consumer);
            this.spinsMetric = spinsResult.metric;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node = super.toJsonObject(node);
            this.spinsMetric.toJsonObject(node.putObject("spins_metric"));
            return node;
        }
    }
}
