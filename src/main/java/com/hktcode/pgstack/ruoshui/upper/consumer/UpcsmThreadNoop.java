/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import org.postgresql.replication.LogSequenceNumber;

public class UpcsmThreadNoop implements UpcsmThread
{
    public static UpcsmThreadNoop of()
    {
        return new UpcsmThreadNoop();
    }

    @Override
    public UpperConsumerRecord poll(long timeout) throws InterruptedException
    {
        Thread.sleep(timeout);
        return null;
    }

    @Override
    public UpcsmReportFetchThread del()
    {
        return null; // TODO:
    }

    @Override
    public UpcsmReportFetchThread pst(LogSequenceNumber lsn)
    {
        return null; // TODO:
    }

    public boolean stop(long timeout)
    {
        return true;
    }

    private UpcsmThreadNoop()
    {
    }
}
