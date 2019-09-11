/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgConfigSnapshot;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.atomic.AtomicReference;

public abstract class UpcsmSender
{
    public final Thread thread;

    protected final AtomicReference<SimpleStatus> status;

    UpcsmSender(Thread thread, AtomicReference<SimpleStatus> status)
    {
        this.thread = thread;
        this.status = status;
    }

    public abstract UpperRecordConsumer poll(long timeout, UpcsmActionRun action) //
        throws InterruptedException;

    public abstract UpcsmReportSender put() throws InterruptedException;

    public abstract UpcsmReportSender get() throws InterruptedException;

    public abstract UpcsmReportSender del() throws InterruptedException;

    public abstract UpcsmReportSender pst(LogSequenceNumber lsn) //
        throws InterruptedException;

    public UpcsmSender pst(PgConfigSnapshot config) throws InterruptedException
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return this;
    }
}
