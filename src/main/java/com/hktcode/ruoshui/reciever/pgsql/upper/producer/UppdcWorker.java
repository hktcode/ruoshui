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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class UppdcWorker extends SimpleWorker //
        implements SimpleWorkerArgval, SimpleWkstepAction
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

    public static UppdcWorker ofJsonObject(JsonNode json, Xqueue<UpperRecordProducer> recver, AtomicLong xidlsn, SimpleAtomic atomic)
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
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        UppdcSender sender = UppdcSender.of(json.path("sender"), xidlsn);
        UppdcWorker result = new UppdcWorker(sender, recver, atomic);
        result.xspins.pst(json.path("xspins"));
        return result;
    }

    public final UppdcSender sender;

    public final Xqueue<UpperRecordProducer> recver;

    public final Xqueue.Spins xspins = Xqueue.Spins.of();

    private UppdcWorker(UppdcSender sender, Xqueue<UpperRecordProducer> recver, SimpleAtomic atomic)
    {
        super(atomic);
        this.sender = sender;
        this.recver = recver;
    }

    @Override
    protected void run(SimpleAtomic atomic) throws Throwable
    {
        this.next(atomic);
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
    public UppdcWorker action()
    {
        return this;
    }

    @Override
    public SimpleWkstep next(SimpleAtomic atomic) //
            throws Throwable
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        List<UpperRecordProducer> lhs, rhs = this.recver.list();
        long now, prelog = System.currentTimeMillis(), spins = 0;
        Iterator<UpperRecordProducer> iter = rhs.iterator();
        Xqueue.Fetch<UpperRecordProducer> recver = this.recver.fetchXqueue();
        try (UppdcSender.Client client = this.sender.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                long l = this.xspins.logDuration;
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
                    this.xspins.spins(spins++);
                }
            }
        }
        return SimpleWkstepTheEnd.of();
    }

    private static final Logger logger = LoggerFactory.getLogger(UppdcWorker.class);
}
