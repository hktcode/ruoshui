/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.init;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.datatype.UpperDatatypeThread;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperSnapshotConfig;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.atomic.AtomicReference;

public class UpperSnapshotInitRecordExecFinish implements UpperSnapshotInitRecord
{
    public static UpperSnapshotInitRecordExecFinish of()
    {
        return new UpperSnapshotInitRecordExecFinish();
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
        metric.fetchThread = UpperDatatypeThread.of(config, pgrepl, status);
        return null;
    }

    private UpperSnapshotInitRecordExecFinish()
    {
    }
}
