/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodPut;
import com.hktcode.bgsimple.method.SimpleMethodPutParamsDefault;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterPut;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgConfig;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgConfigSnapshot;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgThread;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class UpcsmSender
{
    public final PgConfig config;

    public final Thread thread;

    protected final AtomicReference<SimpleStatus> status;

    protected final TransferQueue<PgRecord> tqueue;

    UpcsmSender(PgConfig config)
    {
        this.tqueue = new LinkedTransferQueue<>();
        SimpleMethodPut[] put = new SimpleMethodPut[] {
            SimpleMethodPutParamsDefault.of()
        };
        SimpleStatusOuterPut s = SimpleStatusOuterPut.of(put, new Phaser(2));
        this.status = new AtomicReference<>(s);
        this.thread = new Thread(PgThread.of(config, status, tqueue));
        this.config = config;
    }

    UpcsmSender(UpcsmSender sender)
    {
        this.thread = sender.thread;
        this.tqueue = sender.tqueue;
        this.status = sender.status;
        this.config = sender.config;
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
