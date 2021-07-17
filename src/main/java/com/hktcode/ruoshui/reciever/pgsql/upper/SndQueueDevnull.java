package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class SndQueueDevnull extends SndQueue
{
    public static final class Schema
    {
        public static final ObjectNode SCHEMA;

        static {
            String filename = "SndQueueDevnull.yml";
            SCHEMA=JacksonObject.getFromResource(SndQueue.class, filename);
        }
    }

    public static SndQueueDevnull of(JsonNode json, AtomicLong xidlsn)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        SndQueueDevnull result = new SndQueueDevnull(xidlsn);
        result.pst(json);
        return result;
    }

    @Override
    public Client client()
    {
        return new Client(this);
    }

    @Override
    public Result toJsonResult()
    {
        return new Result(new Config(this), new Metric(this));
    }

    private SndQueueDevnull(AtomicLong xidlsn)
    {
        super(xidlsn);
    }

    @Override
    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
    }

    public static class Client implements SndQueue.Client
    {
        private final SndQueueDevnull squeue;

        private Client(SndQueueDevnull squeue)
        {
            this.squeue = squeue;
        }

        @Override
        public void send(RhsQueue.Record record)
        {
            if (record.val instanceof PgsqlValTxactCommit) {
                long lsn = ((PgsqlValTxactCommit)record.val).lsnofmsg;
                this.squeue.lastConfirm.set(lsn);
                logger.info("commit msg: lsn={}", lsn);
            }
        }

        @Override
        public void close()
        {
        }
    }

    public static class Config extends SndQueue.Config
    {
        private Config(SndQueueDevnull sender)
        {
            super("devnull");
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            return super.toJsonObject(node);
        }
    }

    public static class Metric extends SndQueue.Metric
    {
        private Metric(SndQueue sender)
        {
            super(sender);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SndQueueDevnull.class);
}
