/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.init;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperSnapshotConfig;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.atomic.AtomicReference;

public class UpperSnapshotInitRecordLogicalMsg implements UpperSnapshotInitRecord
{
    public static UpperSnapshotInitRecordLogicalMsg of(long lsn, LogicalMsg msg)
    {
        if(msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new UpperSnapshotInitRecordLogicalMsg(lsn, msg);
    }
    private final long lsn;

    private final LogicalMsg msg;

    @Override
    public UpperConsumerRecord update
        /* */(UpperSnapshotConfig config //
        /* */, PgConnection pgrepl //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */, UpperConsumerMutableMetric metric //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return UpperConsumerRecord.of(lsn, msg);
    }

    private UpperSnapshotInitRecordLogicalMsg(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }
}
