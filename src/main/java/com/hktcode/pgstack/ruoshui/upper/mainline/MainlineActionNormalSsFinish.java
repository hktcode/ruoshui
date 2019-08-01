/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionNormalSsFinish extends MainlineActionNormal
    /* */< MainlineActionNormalSsFinish //
    /* */, MainlineConfigNormal //
    /* */, MainlineMetricNormalSsBegins //
    /* */> //
{
    static MainlineActionNormalSsFinish of(MainlineActionNormalSrBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        MainlineConfigNormal config = action.config;
        MainlineMetricNormalSsBegins metric = MainlineMetricNormalSsBegins.of
            /* */( action.metric.startMillis
                /* */, action.metric.relalist
                /* */, action.metric.relaLock
                /* */, action.metric.replSlot
                /* */, action.metric.sizeDiff
                /* */, action.metric.relationLst
                /* */, action.metric.actionStart
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
        return new MainlineActionNormalSsFinish(config, metric, status, tqueue);
    }

    private static final Logger logger = LoggerFactory.getLogger(MainlineActionNormalSsFinish.class);

    private MainlineActionNormalSsFinish //
        /* */(MainlineConfigNormal config //
        /* */, MainlineMetricNormalSsBegins metric //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */) //
    {
        super(config, metric, status, tqueue);
    }

    public MainlineAction next() throws InterruptedException
    {
        long lsn = this.metric.replSlot.createTuple.consistentPoint;
        List<PgReplRelation> relalist //
            = new ArrayList<>(this.metric.relationLst.size());
        for(PgsqlRelationMetric m : this.metric.relationLst) {
            relalist.add(m.relationInfo);
        }
        LogicalEndSnapshotMsg msg //
            = LogicalEndSnapshotMsg.of(ImmutableList.copyOf(relalist));
        MainlineRecordNormal record = MainlineRecordNormal.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return MainlineActionNormalTypelist.of(this);
            }
        }
        return MainlineActionFinish.of();
    }
}
