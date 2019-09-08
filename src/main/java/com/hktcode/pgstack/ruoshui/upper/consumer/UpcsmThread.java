/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotConfig;
import org.postgresql.replication.LogSequenceNumber;

import javax.script.ScriptException;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class UpcsmThread
{
    protected final UpcsmThreadMainline mlxact;

    protected final Thread thread;

    protected final AtomicReference<SimpleStatus> status;

    protected UpcsmThread //
        /* */( UpcsmThreadMainline mlxact //
        /* */, Thread thread //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        this.mlxact = mlxact;
        this.thread = thread;
        this.status = status;
    }

    protected UpcsmThread //
        /* */( Thread thread //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        this.mlxact = (UpcsmThreadMainline) this;
        this.thread = thread;
        this.status = status;
    }

    public abstract UpperConsumerRecord poll(long timeout, UpcsmActionRun action) //
        throws InterruptedException;

    public abstract UpcsmReportFetchThread put() throws InterruptedException;

    public abstract UpcsmReportFetchThread get() throws InterruptedException;

    public abstract UpcsmReportFetchThread del() throws InterruptedException;

    public abstract UpcsmReportFetchThread pst(LogSequenceNumber lsn) //
        throws InterruptedException;

    public UpcsmThread pst(SnapshotConfig config) //
        throws InterruptedException
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return this;
    }

    // public boolean stop(long timeout) throws InterruptedException
    // {
    //     boolean result = this.thread.isAlive();
    //     if (result) {
    //         return true;
    //     }
    //     this.thread.join(timeout);
    //     return this.thread.isAlive();
    // }
}
