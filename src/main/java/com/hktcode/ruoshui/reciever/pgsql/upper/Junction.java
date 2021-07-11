package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.queue.Xspins;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalTxactContext;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlKey;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlVal;
import com.hktcode.queue.XArray;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Junction extends SimpleWorker
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

    public static Junction //
    of(LhsQueue recver, RhsQueue sender, SimpleAtomic atomic)
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
        return new Junction(recver, sender, atomic);
    }

    // argval

    private final Xspins xspins = Xspins.of();
    private final LhsQueue recver;
    private final RhsQueue sender;

    // gauges

    private long curlsn = 0;
    private long curseq = 0;
    private final LogicalTxactContext xidenv = LogicalTxactContext.of();

    private Junction(LhsQueue recver, RhsQueue sender, SimpleAtomic atomic)
    {
        super(atomic);
        this.recver = recver;
        this.sender = sender;
    }

    @Override
    protected void run(SimpleAtomic atomic) throws InterruptedException
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        XArray<LhsQueue.Record> crhs = recver.newArray(), clhs;
        XArray<RhsQueue.Record> plhs = sender.newArray(), prhs;
        long spins = 0;
        long ln, lt = System.currentTimeMillis();
        Iterator<LhsQueue.Record> citer = crhs.iterator();
        Iterator<RhsQueue.Record> piter = plhs.iterator();
        RhsQueue.Record r = null;
        while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
            long ld = this.xspins.logDuration;
            if (plhs.getSize() > 0 && (prhs = sender.push(plhs)) != plhs) {
                plhs = prhs;
                spins = 0;
                lt = System.currentTimeMillis();
            } else if (r != null) {
                r = plhs.add(r) ? null : r;
            } else if (piter.hasNext()) {
                r = piter.next();
            } else if (citer.hasNext()) {
                piter = this.convert(citer.next()).iterator();
                lt = System.currentTimeMillis();
            } else if ((clhs = recver.poll(crhs)) != crhs) {
                citer = (crhs = clhs).iterator();
            } else if (lt + ld >= (ln = System.currentTimeMillis())) {
                logger.info("logDuration={}", ld);
                lt = ln;
            } else {
                this.xspins.spins(spins++);
            }
        }
        logger.info("upjct complete");
    }

    private List<RhsQueue.Record> convert(LhsQueue.Record record)
    {
        long lsn = record.lsn;
        LogicalMsg msg = record.msg;

        // 根据对PostgreSQL的测试，作为key的lsn不是严格自增长的.
        // 考虑事务B后于事务A开始，但先于事务A提交。
        // 事务A中写入了a和b两条记录，事务B中写入了c和d两条记录。
        // 事务a、b、c、d写入的顺序为a、c、b、d则：
        // 1、事务B的开始lsn大于事务A的lsn。
        // 2、事务B的XID大于事务A的XID
        // 3、a.lsn > c.lsn > b.lsn > d.lsn。
        // 4、事务B先于事务A提交，事务B先于事务A被接收。
        // 5、事务B的提交lsn小于事务A的提交lsn。
        // 此时LSN不是严格自增长.

        if (msg instanceof LogicalTxactBeginsMsg) {
            this.curlsn = ((LogicalTxactBeginsMsg) msg).lsnofcmt;
            this.curseq = 1;
        }

        ImmutableList<PgsqlVal> vallist = PgsqlVal.of(lsn, msg, this.xidenv);
        List<RhsQueue.Record> result = new ArrayList<>();
        for (PgsqlVal val : vallist) {
            PgsqlKey key = PgsqlKey.of(curlsn, curseq++, xidenv.committs);
            RhsQueue.Record d = RhsQueue.Record.of(key, val);
            result.add(d);
        }
        return ImmutableList.copyOf(result);
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

    private static final Logger logger = LoggerFactory.getLogger(Junction.class);

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

    public static class AmendRecordMetric implements JacksonObject
    {
        public final long curlsn;
        public final long curseq;

        private AmendRecordMetric(Junction junction)
        {
            this.curlsn = junction.curlsn;
            this.curseq = junction.curseq;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node.put("curlsn", this.curlsn);
            node.put("curseq", this.curseq);
            return node;
        }
    }

    public static class Metric extends SimpleWorker.Metric
    {
        public final Xspins.Metric spinsMetric;

        public final AmendRecordMetric amendRecord;

        private Metric(Xspins.Result spinsResult, Junction junction)
        {
            super(junction);
            this.spinsMetric = spinsResult.metric;
            this.amendRecord = new AmendRecordMetric(junction);
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            super.toJsonObject(node);
            this.spinsMetric.toJsonObject(node.putObject("spins_metric"));
            this.amendRecord.toJsonObject(node.putObject("amend_record"));
            return node;
        }
    }
}
