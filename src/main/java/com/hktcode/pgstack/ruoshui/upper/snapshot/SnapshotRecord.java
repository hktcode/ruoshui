/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.pgstack.ruoshui.upper.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;

import java.util.concurrent.TransferQueue;

public interface SnapshotRecord
{
    UpperConsumerRecord update //
        /* */(UpperConsumerMetric metric //
        /* */, Thread thread //
        /* */, TransferQueue<SnapshotRecord> tqueue //
        /* */, MainlineThread xact //
        /* */);
}
