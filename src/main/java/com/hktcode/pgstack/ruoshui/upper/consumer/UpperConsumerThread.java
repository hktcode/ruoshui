/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import org.postgresql.replication.LogSequenceNumber;

public interface UpperConsumerThread
{
    UpperConsumerRecord poll(long timeout) throws InterruptedException;

    UpperConsumerReportFetchThread del() throws InterruptedException;

    UpperConsumerReportFetchThread pst(LogSequenceNumber lsn) //
        throws InterruptedException;
}
