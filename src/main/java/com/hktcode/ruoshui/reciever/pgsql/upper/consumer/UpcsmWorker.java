package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.queue.Xqueue.Spins;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class UpcsmWorker //
        implements SimpleWorkerArgval<UpcsmWorker>, SimpleWkstepAction<UpcsmWorker>
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode argvalNode = schema.putObject("properties");
        ObjectNode actionInfosNode = argvalNode.putObject("actions_info");
        actionInfosNode.put("type", "array");
        actionInfosNode.set("items", UpcsmRecver.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpcsmWorker of(JsonNode json, AtomicLong xidlsn) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        Xqueue<UpperRecordConsumer> sender = Xqueue.of(json.path("sender"));
        UpcsmRecver recver = UpcsmRecver.of(json.path("recver"), xidlsn);
        UpcsmWorker result = new UpcsmWorker(recver, sender);
        result.xspins.pst(json.path("xspins"));
        return result;
    }

    public final Spins xspins = Spins.of();

    public final Xqueue<UpperRecordConsumer> sender;

    public final UpcsmRecver recver;

    @Override
    public SimpleWkstep next(SimpleAtomic atomic) //
            throws InterruptedException, SQLException
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        UpperRecordConsumer r;
        int curCapacity = this.sender.maxCapacity;
        List<UpperRecordConsumer> rhs, lhs = new ArrayList<>(curCapacity);
        int spins = 0, spinsStatus = Xqueue.Spins.RESET;
        long now, logtime = System.currentTimeMillis();
        final Xqueue.Offer<UpperRecordConsumer> sender = this.sender.offerXqueue();
        try (UpcsmRecver.Client client = this.recver.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                // 未来计划：此处可以提高性能
                int size = lhs.size();
                int capacity = this.sender.maxCapacity;
                long logDuration = this.xspins.logDuration;
                if (    (size > 0)
                        // 未来计划：支持bufferCount和maxDuration
                        && (rhs = sender.push(lhs)) != lhs
                        && (curCapacity != capacity || (lhs = rhs) == null)
                ) {
                    lhs = new ArrayList<>(capacity);
                    curCapacity = capacity;
                    spins = 0;
                    logtime = System.currentTimeMillis();
                } else if (size < capacity && (r = client.recv()) != null) {
                    lhs.add(r);
                    spins = 0;
                    logtime = System.currentTimeMillis();
                } else if (logtime + logDuration >= (now = System.currentTimeMillis())) {
                    logger.info("logDuration={}", logDuration);
                    logtime = now;
                } else {
                    if (spinsStatus == Xqueue.Spins.SLEEP) {
                        client.forceUpdateStatus();
                    }
                    spinsStatus = this.xspins.spins(spins++);
                }
            }
        }
        logger.info("pgsender complete");
        return SimpleWkstepTheEnd.of();
    }

    private UpcsmWorker(UpcsmRecver recver, Xqueue<UpperRecordConsumer> sender)
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
        // this.actionInfos.get(0).pst(node);
    }

    @Override
    public UpcsmWorker action()
    {
        return this;
    }

    private static final Logger logger = LoggerFactory.getLogger(UpcsmWorker.class);
}
