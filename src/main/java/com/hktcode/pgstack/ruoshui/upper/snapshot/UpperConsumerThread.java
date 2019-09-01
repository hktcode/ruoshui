/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import org.postgresql.replication.LogSequenceNumber;

public interface UpperConsumerThread
{
    default void setTxactionLsn(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
    }

    UpperConsumerRecord poll(long timeout, UpperConsumerMetric metric)
        throws InterruptedException;

    public boolean stop(long timeout) throws InterruptedException;
}
