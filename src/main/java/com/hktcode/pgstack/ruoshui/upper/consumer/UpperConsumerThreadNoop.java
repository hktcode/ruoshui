/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import org.postgresql.replication.LogSequenceNumber;

public class UpperConsumerThreadNoop implements UpperConsumerThread
{
    public static UpperConsumerThreadNoop of()
    {
        return new UpperConsumerThreadNoop();
    }

    @Override
    public UpperConsumerRecord poll(long timeout) throws InterruptedException
    {
        Thread.sleep(timeout);
        return null;
    }

    @Override
    public UpperConsumerReportFetchThread del()
    {
        return null; // TODO:
    }

    @Override
    public UpperConsumerReportFetchThread pst(LogSequenceNumber lsn)
    {
        return null; // TODO:
    }

    public boolean stop(long timeout)
    {
        return true;
    }

    private UpperConsumerThreadNoop()
    {
    }
}
