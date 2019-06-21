/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThreadBasic;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public abstract class UpperTxactionThread //
    extends UpperConsumerThreadBasic<UpperTxactionRecord>
{
    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMutableMetric metric) //
        throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        UpperTxactionRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            return record.update(metric);
        }
        else if (this.thread.isAlive()) {
            return null;
        }
        else {
            // TODO: throw new DelegateNotAliveException();
            throw new RuntimeException();
        }
    }

    protected UpperTxactionThread(Thread thread, TransferQueue<UpperTxactionRecord> tqueue)
    {
        super(thread, tqueue);
    }
}
