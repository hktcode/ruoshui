/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import org.postgresql.replication.LogSequenceNumber;

public interface UpperConsumerThread
{
    default void setTxactionLsn(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
    }

    UpperConsumerRecord poll(long timeout, UpperConsumerMutableMetric metric)
        throws InterruptedException;

    public boolean stop(long timeout) throws InterruptedException;
}
