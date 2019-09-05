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
    public UpcsmReportFetchThread put() throws InterruptedException
    {
        return this.get();
    }

    @Override
    public UpcsmReportFetchThread get()
    {
        return null; // TODO:
    }


    @Override
    public UpcsmReportFetchThread del()
    {
        return this.get(); // TODO:
    }

    @Override
    public UpcsmReportFetchThread pst(LogSequenceNumber lsn)
    {
        return this.get(); // TODO:
    }

    public boolean stop(long timeout)
    {
        return true;
    }

    private UpcsmThreadNoop()
    {
    }
}
