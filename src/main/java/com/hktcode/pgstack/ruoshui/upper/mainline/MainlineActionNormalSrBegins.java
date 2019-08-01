/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegRelationMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionNormalSrBegins extends MainlineActionNormal
    /* */< MainlineActionNormalSrBegins
    /* */, MainlineConfigNormal
    /* */, MainlineMetricNormalSrBegins
    /* */>
{
    static MainlineActionNormalSrBegins of(MainlineActionNormalSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        MainlineConfigNormal config = action.config;
        MainlineMetricNormalSrBegins metric = MainlineMetricNormalSrBegins.of //
            /* */( action.metric.startMillis //
            /* */, action.metric.relalist //
            /* */, action.metric.relaLock //
            /* */, action.metric.replSlot //
            /* */, action.metric.sizeDiff //
            /* */, action.metric.relationLst //
            /* */, action.metric.actionStart //
            /* */, action.metric.relationLst.iterator() //
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
        return new MainlineActionNormalSrBegins(config, metric, status, tqueue);
    }

    static MainlineActionNormalSrBegins of(MainlineActionNormalSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        MainlineConfigNormal config = action.config;
        MainlineMetricNormalSrBegins metric = MainlineMetricNormalSrBegins.of //
            /* */( action.metric.startMillis //
                /* */, action.metric.relalist //
                /* */, action.metric.relaLock //
                /* */, action.metric.replSlot //
                /* */, action.metric.sizeDiff //
                /* */, action.metric.relationLst //
                /* */, action.metric.actionStart //
                /* */, action.metric.relationLst.iterator() //
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
        return new MainlineActionNormalSrBegins(config, metric, status, tqueue);
    }

    private MainlineActionNormalSrBegins //
        /* */(MainlineConfigNormal config //
        /* */, MainlineMetricNormalSrBegins metric //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */) //
    {
        super(config, metric, status, tqueue);
    }

    public MainlineAction next() throws InterruptedException
    {
        if (this.metric.relationLst.isEmpty()) {
            return MainlineActionNormalSsFinish.of(this);
        }
        PgsqlRelationMetric relation = this.metric.iterator.next();
        this.metric.relation = new PgsqlRelationMetric[] { relation };
        PgReplRelation r = relation.relationInfo;

        long lsn = this.metric.replSlot.createTuple.consistentPoint;
        LogicalBegRelationMsg msg = LogicalBegRelationMsg.of(r);
        MainlineRecordNormal record = MainlineRecordNormal.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return MainlineActionNormalTupleval.of(this);
            }
        }
        return MainlineActionFinish.of();
    }
}
