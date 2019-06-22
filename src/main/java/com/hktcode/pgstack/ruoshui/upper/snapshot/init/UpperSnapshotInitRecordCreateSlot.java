/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.init;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperSnapshotConfig;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.atomic.AtomicReference;

public class UpperSnapshotInitRecordCreateSlot implements UpperSnapshotInitRecord
{
    public static UpperSnapshotInitRecordCreateSlot of()
    {
        return new UpperSnapshotInitRecordCreateSlot();
    }
    @Override
    public UpperConsumerRecord update
        /* */( UpperSnapshotConfig config //
        /* */, PgConnection pgrepl //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */, UpperConsumerMutableMetric metric //
        /* */)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return null;
    }

    private UpperSnapshotInitRecordCreateSlot()
    {
    }
}
