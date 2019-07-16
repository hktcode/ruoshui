/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotRecord;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotThreadCreateSlot;

import java.util.concurrent.TransferQueue;

public class PgsqlSnapshotRecordPauseWorld implements PgsqlSnapshotRecord
{
    public static PgsqlSnapshotRecordPauseWorld of()
    {
        return new PgsqlSnapshotRecordPauseWorld();
    }

    private PgsqlSnapshotRecordPauseWorld()
    {
    }
}
