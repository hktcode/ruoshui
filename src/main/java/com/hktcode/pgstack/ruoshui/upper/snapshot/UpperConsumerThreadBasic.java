/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.pgstack.ruoshui.upper.snapshot.UpperConsumerThread;

import java.util.concurrent.TransferQueue;

public abstract class UpperConsumerThreadBasic<T> implements UpperConsumerThread
{
    protected final Thread thread;

    protected final TransferQueue<T> tqueue;

    protected UpperConsumerThreadBasic(Thread thread, TransferQueue<T> tqueue)
    {
        this.thread = thread;
        this.tqueue = tqueue;
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
