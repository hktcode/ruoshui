/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;

import java.util.concurrent.TransferQueue;

public interface SnapshotRecord
{
    UpperConsumerRecord update //
        /* */( UpperConsumerMutableMetric metric //
        /* */, Thread thread //
        /* */, TransferQueue<SnapshotRecord> tqueue //
        /* */, MainlineThreadWork xact //
        /* */);
}
