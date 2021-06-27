package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class UppdcWorkerArgval extends SimpleWorkerGauges //
        implements SimpleWorkerArgval<UppdcWorkerArgval, UppdcWorkerArgval>
                 , SimpleWkstepAction<UppdcWorkerArgval, UppdcWorkerArgval>
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode argvalNode = schema.putObject("properties");
        ObjectNode actionInfosNode = argvalNode.putObject("action_infos");
        actionInfosNode.put("type", "array");
        actionInfosNode.set("items", UppdcSender.Schema.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UppdcWorkerArgval ofJsonObject(JsonNode json, Xqueue<UpperRecordProducer> recver, AtomicLong xidlsn)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (recver == null) {
            throw new ArgumentNullException("recver");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        UppdcSender sender = UppdcSender.of(json.path("sender"), xidlsn);
        UppdcWorkerArgval result = new UppdcWorkerArgval(sender, recver);
        result.xspins.pst(json.path("xspins"));
        return result;
    }

    public final UppdcSender sender;

    public final Xqueue<UpperRecordProducer> recver;

    public final Xqueue.Spins xspins = Xqueue.Spins.of();

    private UppdcWorkerArgval(UppdcSender sender, Xqueue<UpperRecordProducer> recver)
    {
        this.sender = sender;
        this.recver = recver;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        return node;
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
    }

    @Override
    public UppdcWorkerArgval action()
    {
        return this;
    }


    @Override
    public SimpleWkstep next(UppdcWorkerArgval argval, UppdcWorkerArgval gauges, SimpleAtomic atomic) ///
            throws Throwable
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (gauges == null) {
            throw new ArgumentNullException("gauges");
        }
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        List<UpperRecordProducer> lhs, rhs = argval.recver.list();
        long now, prelog = System.currentTimeMillis(), spins = 0;
        Iterator<UpperRecordProducer> iter = rhs.iterator();
        Xqueue.Fetch<UpperRecordProducer> recver = argval.recver.fetchXqueue();
        try (UppdcSender.Client client = argval.sender.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                long l = argval.xspins.logDuration;
                if (iter.hasNext()) {
                    // 未来计划：send方法支持数组，发送多个记录，提高性能
                    client.send(iter.next());
                } else if ((lhs = recver.poll(rhs)) != rhs) {
                    rhs = lhs;
                    iter = rhs.iterator();
                } else if (prelog + l >= (now = System.currentTimeMillis())) {
                    logger.info("write to logDuration={}", l);
                    prelog = now;
                } else {
                    gauges.xspins.spins(spins++);
                }
            }
        }
        return SimpleWkstepTheEnd.of();
    }

    private static final Logger logger = LoggerFactory.getLogger(UppdcWorkerArgval.class);
}
