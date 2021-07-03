package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class UppdcSender
{
    public static final class Schema
    {
        public static final ObjectNode SCHEMA;

        static
        {
            ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
            schema.put("$schema", "http://json-schema.org/draft-04/schema#");
            ObjectNode typeNode = schema.putObject("type");
            ArrayNode oneOfNode = typeNode.putArray("oneOf");
            oneOfNode.add(UppdcSenderFiles.Schema.SCHEMA);
            oneOfNode.add(UppdcSenderKafka.Schema.SCHEMA);
            SCHEMA = JacksonObject.immutableCopy(schema);
        }
    }

    public static UppdcSender of(JsonNode json, AtomicLong xidlsn)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        String senderClass = json.path("sender_class").asText("files");
        UppdcSender result;
        if (senderClass.equals("kafka")) {
            result = UppdcSenderKafka.of(json, xidlsn);
        }
        else {
            result = UppdcSenderFiles.of(json, xidlsn);
        }
        return result;

    }

    public abstract Client client();

    public abstract Result toJsonResult();

    // gauges
    public long offerTrycnt = 0;

    public long offerRowcnt = 0;

    public long offerCounts = 0;

    public final AtomicReference<Throwable> callbackRef = new AtomicReference<>();

    public final AtomicLong txactionLsn;

    public interface Client extends AutoCloseable
    {
        void send(UpperRecordProducer record) throws Throwable;
    }

    public static class Result implements JacksonObject
    {
        public final Config config;

        public final Metric metric;

        protected Result(Config config, Metric metric)
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

        public final long txactionLsn;

        protected Metric(UppdcSender sender)
        {
            this.offerTrycnt = sender.offerTrycnt;
            this.offerRowcnt = sender.offerRowcnt;
            this.offerCounts = sender.offerCounts;
            this.txactionLsn = sender.txactionLsn.get();
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
            node.put("txaction_lsn", this.txactionLsn);
            // - node.put("last_confirm", this.txactionLsn);
            return node;
        }
    }

    protected UppdcSender(AtomicLong xidlsn)
    {
        this.txactionLsn = xidlsn;
    }
}
