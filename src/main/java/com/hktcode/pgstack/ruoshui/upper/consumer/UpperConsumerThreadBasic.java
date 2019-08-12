/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.status.SimpleStatus;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class UpperConsumerThreadBasic<T> implements UpperConsumerThread
{
    protected final Thread thread;

    protected final TransferQueue<T> tqueue;

    protected final AtomicReference<SimpleStatus> status;

    protected UpperConsumerThreadBasic //
        /* */( Thread thread //
        /* */, TransferQueue<T> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        this.thread = thread;
        this.tqueue = tqueue;
        this.status = status;
    }

    public boolean stop(long timeout) throws InterruptedException
    {
        boolean result = this.thread.isAlive();
        if (result) {
            return true;
        }
        this.thread.join(timeout);
        return this.thread.isAlive();
    }
}
