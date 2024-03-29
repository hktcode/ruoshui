package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalReplArgval;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgProps;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public class RcvQueue
{
    public static final class Schema
    {
        public final static ObjectNode SCHEMA;

        static {
            String filename = "RcvQueue.yml";
            SCHEMA = JacksonObject.getFromResource(RcvQueue.class, filename);
        }
    }

    public static RcvQueue of(JsonNode json, AtomicLong xidlsn) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        JsonNode srcPropertyNode = json.path("src_property");
        PgProps srcProperty = PgProps.ofJsonObject(srcPropertyNode);

        JsonNode logicalReplNode = json.path("logical_repl");
        LogicalReplArgval logicalRepl = LogicalReplArgval.of(logicalReplNode);

        return new RcvQueue(srcProperty, logicalRepl, xidlsn);
    }

    // argval

    private final PgProps srcProperty;

    private final LogicalReplArgval logicalRepl;

    // gauges

    private long trycnt = 0;

    private long rowcnt = 0;

    private final AtomicLong lastConfirm;

    private RcvQueue //
        /**/( PgProps srcProperty
            , LogicalReplArgval logicalRepl
            , AtomicLong xidlsn
            )
    {
        this.srcProperty = srcProperty;
        this.logicalRepl = logicalRepl;
        this.lastConfirm = xidlsn;
    }

    public Client client() throws SQLException
    {
        return new Client(this);
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
    }

    public Result toJsonResult()
    {
        return new Result(this);
    }

    public static class Client implements AutoCloseable
    {
        private final RcvQueue recver;

        private final Connection pgrepl;

        private final PGReplicationStream stream;

        private long prelsn = 0;

        private Client(RcvQueue recver) throws SQLException
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

        public LhsQueue.Record recv() throws SQLException
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
            return LhsQueue.Record.of(key, val);
        }

        public void forceUpdateStatus() throws SQLException
        {
            this.updateReportLSN();
            this.stream.forceUpdateStatus();
        }

        public void updateReportLSN()
        {
            long n = this.recver.lastConfirm.get();
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

    public static final class Result extends JsonResult<Config, Metric>
    {
        private Result(RcvQueue recver)
        {
            super(new Config(recver), new Metric(recver));
        }
    }

    public static final class Config implements JacksonObject
    {
        public final PgProps srcProperty;

        public final LogicalReplArgval logicalRepl;

        private Config(RcvQueue recver)
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
            ObjectNode logicalReplNode = node.putObject("logical_repl");
            this.logicalRepl.toJsonObject(logicalReplNode);
            ObjectNode srcPropertyNode = node.putObject("src_property");
            this.srcProperty.toJsonObject(srcPropertyNode);
            return node;
        }
    }

    public static final class Metric implements JacksonObject
    {
        public final long fetchTrycnt;

        public final long fetchRowcnt;

        private Metric(RcvQueue recver)
        {
            this.fetchTrycnt = recver.trycnt;
            this.fetchRowcnt = recver.rowcnt;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node.put("fetch_trycnt", this.fetchTrycnt);
            node.put("fetch_rowcnt", this.fetchRowcnt);
            return node;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(RcvQueue.class);
}
