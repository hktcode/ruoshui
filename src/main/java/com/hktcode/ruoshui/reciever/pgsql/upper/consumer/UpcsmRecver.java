package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalReplArgval;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgConnectionProperty;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public class UpcsmRecver
{
    public static final class Schema
    {
        public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UpcsmRecver.class, "UpcsmRecver.yml");
    }

    public static UpcsmRecver of(JsonNode json, AtomicLong xidlsn) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        // - JsonNode srcPropertyNode = json.path("sender_class");
        JsonNode srcPropertyNode = json.path("src_property");
        PgConnectionProperty srcProperty = PgConnectionProperty.ofJsonObject(srcPropertyNode);

        JsonNode logicalReplNode = json.path("logical_repl");
        LogicalReplArgval logicalRepl = LogicalReplArgval.of(logicalReplNode);

        return new UpcsmRecver(srcProperty, logicalRepl, xidlsn);
    }

    // argval

    public final PgConnectionProperty srcProperty;

    public final LogicalReplArgval logicalRepl;
    // - public final LogicalReplArgval senderProps;
    // - public final LogicalReplArgval recverProps;

    // gauges

    public long trycnt = 0;

    public long rowcnt = 0;

    private final AtomicLong txactionLsn;

    private UpcsmRecver(PgConnectionProperty srcProperty, LogicalReplArgval logicalRepl, AtomicLong xidlsn)
    {
        this.srcProperty = srcProperty;
        this.logicalRepl = logicalRepl;
        this.txactionLsn = xidlsn;
    }

    public Client client() throws SQLException
    {
        return new Client(this);
    }

    public Result toJsonResult()
    {
        return new Result(this);
    }

    public static class Client implements AutoCloseable
    {
        private final UpcsmRecver recver;

        private final Connection pgrepl;

        private final PGReplicationStream stream;

        private long prelsn = 0;

        private Client(UpcsmRecver recver) throws SQLException
        {
            this.recver = recver;
            Connection conn = recver.srcProperty.replicaConnection();
            try {
                this.pgrepl = conn;
                PgConnection pgrepl = conn.unwrap(PgConnection.class);
                this.stream = recver.logicalRepl.start(pgrepl);
            } catch (SQLException ex) {
                conn.close();
                throw ex;
            }
        }

        public UpperRecordConsumer recv() throws SQLException
        {
            this.updateReportLSN();
            ByteBuffer msg = this.stream.readPending();
            ++this.recver.trycnt;
            if (msg == null) {
                return null;
            }
            long key = this.stream.getLastReceiveLSN().asLong();
            LogicalMsg val = LogicalMsg.ofLogicalWal(msg);
            ++this.recver.rowcnt;
            return UpperRecordConsumer.of(key, val);
        }

        public void forceUpdateStatus() throws SQLException
        {
            this.updateReportLSN();
            this.stream.forceUpdateStatus();
        }

        public void updateReportLSN()
        {
            long n = this.recver.txactionLsn.get();
            if (n == this.prelsn) {
                return;
            }
            LogSequenceNumber lsn = LogSequenceNumber.valueOf(n);
            this.stream.setFlushedLSN(lsn);
            this.stream.setAppliedLSN(lsn);
            this.prelsn = n;
        }

        @Override
        public void close() throws SQLException
        {
            try {
                this.stream.close();
            }
            finally {
                this.pgrepl.close();
            }
        }
    }

    public static final class Result implements JacksonObject
    {
        public final Config config;

        public final Metric metric;

        private Result(UpcsmRecver recver)
        {
            this.config = new Config(recver);
            this.metric = new Metric(recver);
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            return null;
        }
    }

    public static final class Config implements JacksonObject
    {
        public final PgConnectionProperty srcProperty;

        public final LogicalReplArgval logicalRepl;

        private Config(UpcsmRecver recver)
        {
            this.srcProperty = recver.srcProperty;
            this.logicalRepl = recver.logicalRepl;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            return node;
        }
    }

    public static final class Metric implements JacksonObject
    {
        public final long fetchTrycnt;

        public final long fetchRowcnt;

        private Metric(UpcsmRecver recver)
        {
            this.fetchTrycnt = recver.trycnt;
            this.fetchRowcnt = recver.rowcnt;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            return null;
        }
    }
    // - @Override
    // - public ObjectNode toJsonObject(ObjectNode node)
    // - {
    // -     if (node == null) {
    // -         throw new ArgumentNullException("node");
    // -     }
    // -     node = super.toJsonObject(node);
    // -     ObjectNode logicalReplNode = node.putObject("logical_repl");
    // -     this.logicalRepl.toJsonObject(logicalReplNode);
    // -     ObjectNode srcPropertyNode = node.putObject("src_property");
    // -     this.srcProperty.toJsonObject(srcPropertyNode);
    // -     return node;
    // - }
}
