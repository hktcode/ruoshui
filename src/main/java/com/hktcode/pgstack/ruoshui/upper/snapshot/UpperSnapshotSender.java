/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotSender;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerSender;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;

import java.time.ZonedDateTime;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class UpperSnapshotSender<T>
    extends UpperConsumerSender<T, UpperRunnableMetric>
    implements PgSnapshotSender<UpperSnapshotMetric>
{
    protected UpperSnapshotSender
        /* */( TransferQueue<T> tqueue
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */)
    {
        super(tqueue, status);
    }

    @Override
    public UpperSnapshotMetric snapshotMetric(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return UpperSnapshotMetric.of(startMillis);
    }

    @Override
    public void sendStatusInfo(String statusInfo, UpperSnapshotMetric metric)
    {
        if (statusInfo == null) {
            throw new ArgumentNullException("statusInfo");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        metric.statusInfor = statusInfo;
    }
}
