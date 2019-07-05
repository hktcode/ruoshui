/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.bgsimple.triple.TripleConsumer;
import com.hktcode.bgsimple.triple.TripleMethodResult;
import com.hktcode.lang.RunnableWithInterrupted;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionInfo;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotThreadLockingRel;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig.DEFAULT_ATTRINFO_SQL;
import static com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig.DEFAULT_RELATION_SQL;

public class UpperConsumer extends TripleConsumer
    /* */< UpperConsumer
    /* */, MainlineConfig
    /* */, UpperConsumerMetric
    /* */, UpperConsumerRecord
    /* */> //
    implements RunnableWithInterrupted
{
    public static UpperConsumer of //
        /* */( MainlineConfig config //
        /* */, AtomicReference<SimpleStatus> status //
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
        /* */( MainlineConfig config //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(config, comein, status);
    }

    @Override
    public void runWithInterrupted() throws InterruptedException
    {
        ZonedDateTime startMillis = ZonedDateTime.now();
        UpperConsumerMetric metric = UpperConsumerMetric.of(startMillis);
        try {
            super.run("upper-consumer", this, metric);
        }
        finally {
            metric.statusInfor = "waiting fetch action stop";
            logger.info("{}", metric.statusInfor);
            long logDuration = this.config.logDuration;
            while (super.newStatus(this, metric) instanceof SimpleStatusInnerEnd
                && !metric.fetchThread.stop(config.logDuration)) {
                long currMillis = System.currentTimeMillis();
                if (currMillis - metric.logDatetime >= logDuration) {
                    logger.info("poll action do not stop, I will retry.");
                    metric.logDatetime = currMillis;
                }
            }
            metric.statusInfor = "upper consumer stopped";
            logger.info("{}", metric.statusInfor);
        }
    }

    @Override
    protected void runInternal(UpperConsumer worker, UpperConsumerMetric metric) throws Exception
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        try (Connection c = this.config.srcProperty.replicaConnection()) {
            PgConnection pgc = c.unwrap(PgConnection.class);
            metric.pgreplInfor = PgConnectionInfo.of(pgc);
            metric.fetchThread = MainlineThread.of(config, status);
            logger.info("upper consumer starts: pgreplInfor={}", metric.pgreplInfor);
            UpperConsumerRecord r = null;
            while (super.newStatus(worker, metric) instanceof SimpleStatusInnerRun) {
                r = (r == null ? this.poll(metric) : this.push(r, metric));
            }
        }
    }

    private UpperConsumerRecord poll(UpperConsumerMetric metric)
        throws InterruptedException
    {
        long waitTimeout = this.config.waitTimeout;
        long logDuration = this.config.logDuration;
        metric.statusInfor = "fetch record wait";
        long startMillis = System.currentTimeMillis();
        UpperConsumerRecord r = metric.fetchThread.poll(waitTimeout, metric);
        long finishMillis = System.currentTimeMillis();
        metric.fetchMillis += (startMillis - finishMillis);
        metric.statusInfor = "fetch record end";
        ++metric.fetchCounts;
        if (r != null) {
            metric.logDatetime = finishMillis;
        }
        else if (finishMillis - metric.logDatetime >= logDuration) {
            logger.info("readPending() returns null: waitTimeout={}, logDuration={}", waitTimeout, logDuration);
            metric.logDatetime = finishMillis;
        }
        return r;
    }

    public TripleMethodResult<UpperConsumer, MainlineConfig, UpperConsumerMetric>
    pst(LogSequenceNumber lastReceiveLsn, UpperConsumerMetric metric)
    {
        if (lastReceiveLsn == null) {
            throw new ArgumentNullException("lastReceiveLsn");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        metric.fetchThread.setTxactionLsn(lastReceiveLsn);
        ++metric.recordCount;
        JsonNode c = this.config.toJsonObject();
        JsonNode m = metric.toJsonObject();
        return TripleMethodResult.of(c, m);
    }

    public TripleMethodResult<UpperConsumer, MainlineConfig, UpperConsumerMetric>
    pstWithSnapshot(JsonNode json, PgSnapshotFilter whereScript, UpperConsumerMetric metric)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode configNode = this.config.toJsonObject();
        JsonNode metricNode = metric.toJsonObject();
        UpperConsumerThread pollAction = metric.fetchThread;
        if (!(pollAction instanceof MainlineThreadWork)) {
            // TODO:
            return TripleMethodResult.of(configNode, metricNode);
        }
        MainlineThreadWork oldAction = (MainlineThreadWork)pollAction;
        PgConnectionProperty s = this.config.srcProperty;
        String p = this.config.logicalRepl.slotName;
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
        metric.fetchThread = SnapshotThreadLockingRel.of(c, status, oldAction);
        return TripleMethodResult.of(configNode, metricNode);
    }
}
