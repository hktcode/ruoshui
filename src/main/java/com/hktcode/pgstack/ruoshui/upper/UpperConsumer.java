/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.SimpleBasicPstBgResult;
import com.hktcode.bgsimple.SimpleDelDefaultBgParams;
import com.hktcode.bgsimple.SimplePstSuccessBgResult;
import com.hktcode.bgsimple.SimpleUnkFailureBgResult;
import com.hktcode.bgtriple.naive.NaiveConsumer;
import com.hktcode.bgtriple.naive.NaiveConsumerMetric;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.bgtriple.status.TripleDelBgStatus;
import com.hktcode.bgtriple.status.TripleEndBgStatus;
import com.hktcode.bgtriple.status.TripleRunBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionInfo;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerConfig;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.snapshot.post.UpperSnapshotPostThreadLockingRel;
import com.hktcode.pgstack.ruoshui.upper.txaction.UpperTxactionThread;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig.DEFAULT_ATTRINFO_SQL;
import static com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig.DEFAULT_RELATION_SQL;

public class UpperConsumer extends NaiveConsumer
    /* */< UpperConsumer
    /* */, UpperJunction
    /* */, UpperProducer
    /* */, UpperConsumerConfig
    /* */, UpperConsumerMutableMetric
    /* */, UpperConsumerRecord
    /* */> //
{
    public static UpperConsumer of //
        /* */( UpperConsumerConfig config //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (comein == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpperConsumer(config, comein, status);
    }

    private static final Logger logger = LoggerFactory.getLogger(UpperConsumer.class);

    private UpperConsumer //
        /* */( UpperConsumerConfig config //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */)
    {
        super(config, UpperConsumerMutableMetric.of(), comein, status);
    }

    @Override
    public void runInternal() throws SQLException, InterruptedException
    {
        UpperConsumerMutableMetric metric = UpperConsumerMutableMetric.of();
        try (Connection c = this.config.srcProperty.replicaConnection()) {
            PgConnection pgc = c.unwrap(PgConnection.class);
            metric.pgreplInfor = PgConnectionInfo.of(pgc);
            metric.fetchThread = this.config.createAction(pgc, this.status);
            logger.info("upper consumer starts: pgreplInfor={}", metric.pgreplInfor);
            this.metric = metric;
            UpperConsumerRecord r = null;
            while (super.newStatus() instanceof TripleRunBgStatus) {
                r = (r == null ? this.poll(metric) : this.push(r));
            }
        }
        catch (Exception ex) {
            logger.error("consumer throws exception: ", ex);
            ZonedDateTime endtime = ZonedDateTime.now();
            String msg = ex.getMessage();
            metric.statusInfor = "throw exception at " + endtime + ": " + msg;
            SimpleUnkFailureBgResult<UpperConsumerConfig, NaiveConsumerMetric, UpperConsumer> c //
                = SimpleUnkFailureBgResult.of(ex, this.config, this.metric.toMetric(), ZonedDateTime.now());
            SimpleDelDefaultBgParams<UpperJunction> j = SimpleDelDefaultBgParams.of();
            SimpleDelDefaultBgParams<UpperProducer> p = SimpleDelDefaultBgParams.of();
            TripleDelBgStatus<UpperConsumer, UpperJunction, UpperProducer> del = TripleDelBgStatus.of(c, j, p);
            TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer> origin;
            TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer> future;
            while (!((origin = super.newStatus(ex, endtime)) instanceof TripleEndBgStatus)) {
                future = origin.del(del);
                this.status.compareAndSet(origin, future);
                metric.statusInfor = "waiting status set to end";
            }
            metric.statusInfor = "consumer finish end";
            logger.info("consumer finish by exception.");
            throw ex;
        }
        finally {
            metric.statusInfor = "waiting fetch action stop";
            logger.info("{}", metric.statusInfor);
            long logDuration = this.config.logDuration;
            while (super.newStatus() instanceof TripleRunBgStatus
                && !metric.fetchThread.stop(config.logDuration)) {
                long currMillis = System.currentTimeMillis();
                if (currMillis - this.metric.logDatetime >= logDuration) {
                    logger.info("poll action do not stop, I will retry.");
                    this.metric.logDatetime = currMillis;
                }
            }
            metric.statusInfor = "upper consumer stopped";
            logger.info("{}", metric.statusInfor);
        }
    }

    private UpperConsumerRecord poll(UpperConsumerMutableMetric metric)
        throws InterruptedException
    {
        long waitTimeout = this.config.waitTimeout;
        long logDuration = this.config.logDuration;
        metric.statusInfor = "fetch record wait";
        long startMillis = System.currentTimeMillis();
        UpperConsumerRecord r = metric.fetchThread.poll(waitTimeout, metric);
        long finishMillis = System.currentTimeMillis();
        this.metric.fetchMillis += (startMillis - finishMillis);
        metric.statusInfor = "fetch record end";
        ++this.metric.fetchCounts;
        if (r != null) {
            this.metric.logDatetime = finishMillis;
        }
        else if (finishMillis - this.metric.logDatetime >= logDuration) {
            logger.info("readPending() returns null: waitTimeout={}, logDuration={}", waitTimeout, logDuration);
            this.metric.logDatetime = finishMillis;
        }
        return r;
    }

    public SimpleBasicPstBgResult<UpperConsumer> pst(LogSequenceNumber lastReceiveLsn)
    {
        if (lastReceiveLsn == null) {
            throw new ArgumentNullException("lastReceiveLsn");
        }
        this.metric.fetchThread.setTxactionLsn(lastReceiveLsn);
        ++this.metric.recordCount;
        return SimplePstSuccessBgResult.of();
    }

    public SimpleBasicPstBgResult<UpperConsumer> //
    pstWithSnapshot(JsonNode json, PgSnapshotFilter whereScript)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        UpperConsumerThread pollAction = this.metric.fetchThread;
        if (!(pollAction instanceof UpperTxactionThread)) {
            // TODO:
            return SimplePstSuccessBgResult.of();
        }
        PgConnectionProperty s = this.config.srcProperty;
        String p = this.config.logicalRepl.slotName;
        UpperTxactionThread oldAction = (UpperTxactionThread)pollAction;
        JsonNode tupleSelectNode = json.path("tuple_select");
        String m = json.path("metadata_sql").asText(DEFAULT_RELATION_SQL);
        String a = json.path("attrinfo_sql").asText(DEFAULT_ATTRINFO_SQL);
        Map<PgReplRelationName, String> map = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = tupleSelectNode.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            PgReplRelationName relationName = PgReplRelationName.ofTextString(e.getKey());
            map.put(relationName, e.getValue().asText());
        }
        ImmutableMap<PgReplRelationName, String> t = ImmutableMap.copyOf(map);
        PgSnapshotConfig c = PgSnapshotConfig.of(s, t, whereScript, m, a, true, p);
        c.rsFetchsize = json.path("rs_fetchsize").asInt(128);
        c.waitTimeout = json.path("wait_timeout").asLong(this.config.waitTimeout);
        this.metric.fetchThread = UpperSnapshotPostThreadLockingRel.of(c, status, oldAction);
        return SimplePstSuccessBgResult.of();
    }
}
