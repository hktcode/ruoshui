/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionNormalSsBegins extends MainlineActionNormal //
    /* */< MainlineActionNormalSsBegins //
    /* */, MainlineConfigNormal //
    /* */, MainlineMetricNormalSsBegins //
    /* */>
{
    static MainlineActionNormalSsBegins of(MainlineActionNormalSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        long finish = System.currentTimeMillis();
        MainlineConfigNormal config = action.config;
        MainlineReportSizeDiff sizeDiff //
            = MainlineReportSizeDiff.of(finish - action.metric.actionStart);
        MainlineMetricNormalSsBegins metric = MainlineMetricNormalSsBegins.of
            /* */( action.metric.startMillis
            /* */, action.metric.relalist
            /* */, action.metric.relaLock
            /* */, action.metric.replSlot
            /* */, sizeDiff
            /* */, action.metric.oldRelalist
            /* */, finish
            /* */);

        return new MainlineActionNormalSsBegins
            (config, metric, action.status, action.tqueue);

    }

    private MainlineActionNormalSsBegins //
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
        LogicalBegSnapshotMsg msg //
            = LogicalBegSnapshotMsg.of(ImmutableList.copyOf(relalist));
        MainlineRecordNormal record = MainlineRecordNormal.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return MainlineActionNormalSrBegins.of(this);
            }
        }
        return MainlineActionFinish.of();
    }
}
