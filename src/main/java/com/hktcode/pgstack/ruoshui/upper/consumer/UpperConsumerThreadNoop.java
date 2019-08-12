/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
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
    public String del()
    {
        return "miscarry";
    }

    @Override
    public void pst(LogSequenceNumber lsn)
    {
    }

    public boolean stop(long timeout)
    {
        return true;
    }

    private UpperConsumerThreadNoop()
    {
    }
}
