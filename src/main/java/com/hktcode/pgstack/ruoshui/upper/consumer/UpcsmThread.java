/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import org.postgresql.replication.LogSequenceNumber;

public interface UpcsmThread
{
    UpperConsumerRecord poll(long timeout) throws InterruptedException;

    UpcsmReportFetchThread put() throws InterruptedException;

    UpcsmReportFetchThread get() throws InterruptedException;

    UpcsmReportFetchThread del() throws InterruptedException;

    UpcsmReportFetchThread pst(LogSequenceNumber lsn) //
        throws InterruptedException;

}
