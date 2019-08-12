/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import org.postgresql.replication.LogSequenceNumber;

public interface UpperConsumerThread
{
    UpperConsumerRecord poll(long timeout) throws InterruptedException;

    String del();

    void pst(LogSequenceNumber lsn);
}
