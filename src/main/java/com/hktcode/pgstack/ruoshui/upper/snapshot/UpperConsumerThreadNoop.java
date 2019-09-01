/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;

public class UpperConsumerThreadNoop implements UpperConsumerThread
{
    public static UpperConsumerThreadNoop of()
    {
        return new UpperConsumerThreadNoop();
    }

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMetric metric) //
        throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        Thread.sleep(timeout);
        return null;
    }

    @Override
    public boolean stop(long timeout) throws InterruptedException
    {
        Thread.sleep(timeout);
        return true;
    }

    private UpperConsumerThreadNoop()
    {
    }
}
