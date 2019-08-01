/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndRelationMsg;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionNormalSrFinish extends MainlineActionNormal
    /* */< MainlineActionNormalSrFinish //
    /* */, MainlineConfigNormal //
    /* */, MainlineMetricNormalTupleval //
    /* */> //
{
    static MainlineActionNormalSrFinish of(MainlineActionNormalTupleval action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        MainlineConfigNormal config = action.config;
        MainlineMetricNormalTupleval metric = action.metric;
        AtomicReference<SimpleStatus> status = action.status;
        TransferQueue<MainlineRecord> tqueue = action.tqueue;
        return new MainlineActionNormalSrFinish(config, metric, status, tqueue);
    }

    protected MainlineActionNormalSrFinish //
        /* */(MainlineConfigNormal config //
        /* */, MainlineMetricNormalTupleval metric //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */) //
    {
        super(config, metric, status, tqueue);
    }

    public MainlineAction next() throws InterruptedException
    {
        long lsn = this.metric.replSlot.createTuple.consistentPoint;
        LogicalEndRelationMsg msg //
            = LogicalEndRelationMsg.of(this.metric.relation.relationInfo);
        MainlineRecordNormal record = MainlineRecordNormal.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return MainlineActionNormalSrBegins.of(this);
            }
        }
        return MainlineActionFinish.of();
    }
}
