/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalCreateTupleMsg;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgjdbc.PgReplComponent;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionNormalTupleval extends MainlineActionNormal //
    /* */< MainlineActionNormalTupleval //
    /* */, MainlineConfigNormal //
    /* */, MainlineMetricNormalTupleval //
    /* */> //
{
    private static final Logger logger = LoggerFactory.getLogger(MainlineActionNormalTupleval.class);

    static MainlineActionNormalTupleval of(MainlineActionNormalSrBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        MainlineConfigNormal config = action.config;
        MainlineMetricNormalTupleval metric = MainlineMetricNormalTupleval.of
            /* */( action.metric.startMillis
            /* */, action.metric.relalist
            /* */, action.metric.relaLock
            /* */, action.metric.replSlot
            /* */, action.metric.sizeDiff
            /* */, action.metric.relationLst
            /* */, action.metric.actionStart
            /* */, action.metric.iterator
            /* */, action.metric.relation[0]
            /* */);
        metric.fetchCounts += action.metric.fetchCounts;
        metric.recordCount += action.metric.recordCount;
        metric.fetchCounts += action.metric.fetchCounts;
        metric.fetchMillis += action.metric.fetchMillis;
        metric.offerCounts += action.metric.offerCounts;
        metric.offerMillis += action.metric.offerMillis;
        metric.logDatetime = action.metric.logDatetime;
        AtomicReference<SimpleStatus> status = action.status;
        TransferQueue<MainlineRecord> tqueue = action.tqueue;
        return new MainlineActionNormalTupleval(config, metric, status, tqueue);
    }

    private MainlineActionNormalTupleval //
        /* */(MainlineConfigNormal config //
        /* */, MainlineMetricNormalTupleval metric //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        super(config, metric, status, tqueue);
    }

    MainlineAction next(ExecutorService exesvc, PgConnection pgdata) //
        throws Exception
    {
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        PgReplRelation r = this.metric.relation.relationInfo;
        final long lsn = this.metric.replSlot.createTuple.consistentPoint;
        try (PreparedStatement ps = this.config.queryTupleval(pgdata, r)) {
            MainlineRecordNormal record = null;
            ResultSet rs = null;
            Boolean next = null;
            Future<ResultSet> rsFuture = exesvc.submit(MainlineDeputeExecuteQuery.of(ps));
            Future<Boolean> nextFuture = null;
            MainlineDeputeResultSetNext rsDepute = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (rs == null) {
                    rs = this.pollFromFuture(rsFuture);
                } else if (rsDepute == null) {
                    rsDepute = MainlineDeputeResultSetNext.of(rs);
                } else if (record != null) {
                    record = this.sendCreateTuple(record);
                } else if (nextFuture == null) {
                    nextFuture = exesvc.submit(rsDepute);
                } else if (next == null) {
                    next = this.pollFromFuture(nextFuture);
                } else if (next) {
                    LogicalCreateTupleMsg msg = this.build(rs);
                    record = MainlineRecordNormal.of(lsn, msg);
                    nextFuture = null;
                    next = null;
                } else {
                    return MainlineActionNormalSrFinish.of(this);
                }
            }
        }
        return MainlineActionFinish.of();
    }

    private LogicalCreateTupleMsg build(ResultSet rs) throws SQLException
    {
        PgReplRelation relation = this.metric.relation.relationInfo;
        final JsonNode oldvalue = MissingNode.getInstance();
        ++this.metric.relation.tuplevalSize;
        List<PgReplComponent> tuple = new ArrayList<>();
        for (PgReplAttribute attr : relation.attrlist) {
            String v = rs.getString(attr.attrname);
            JsonNode newvalue = (v == null ? NullNode.getInstance() : TextNode.valueOf(v));
            tuple.add(PgReplComponent.of(attr, oldvalue, newvalue));
        }
        return LogicalCreateTupleMsg.of(relation, ImmutableList.copyOf(tuple));
    }

    public MainlineRecordNormal sendCreateTuple(MainlineRecordNormal record)
        throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long startsMillis = System.currentTimeMillis();
        metric.statusInfor = "offer record wait";
        boolean success = this.tqueue.tryTransfer(record, waitTimeout, TimeUnit.MILLISECONDS);
        metric.statusInfor = "offer record end";
        long finishMillis = System.currentTimeMillis();
        metric.offerMillis += (finishMillis - startsMillis);
        ++metric.offerCounts;
        if (success) {
            ++metric.recordCount;
            return null;
        }
        else {
            long logDuration = config.logDuration;
            long currMillis = System.currentTimeMillis();
            if (currMillis - metric.logDatetime >= logDuration) {
                logger.info("tryTransfer record to tqueue: waitTimeout={}, logDuration={}", waitTimeout, logDuration);
                metric.logDatetime = currMillis;
            }
            return record;
        }
    }
}
