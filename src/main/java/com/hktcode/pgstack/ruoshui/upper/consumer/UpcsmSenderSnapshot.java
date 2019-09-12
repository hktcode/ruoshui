/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.future.*;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.*;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public abstract class UpcsmSenderSnapshot extends UpcsmSender
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmSenderSnapshot.class);

    public final UpcsmSenderMainline mlxact;

    public final UpperRecordConsumer record;

    protected UpcsmSenderSnapshot(UpcsmSenderSnapshot sender, UpperRecordConsumer record)
    {
        super(sender);
        this.mlxact = sender.mlxact;
        this.record = record;
    }

    protected UpcsmSenderSnapshot(UpcsmSenderSnapshot sender)
    {
        super(sender);
        this.mlxact = sender.mlxact;
        this.record = null;
    }

    protected UpcsmSenderSnapshot(UpcsmSenderMainline mlxact, PgConfig config)
    {
        super(config);
        this.mlxact = mlxact;
        this.record = null;
    }

    @Override
    public UpcsmReportSender put() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleFuturePut future = holder.put();
        thread.start();
        UpcsmReportSender ml = this.mlxact.get();
        return this.build(ml, future);
    }

    @Override
    public UpcsmReportSender get() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodGet[] params = new SimpleMethodGet[] {
            SimpleMethodGetParamsDefault.of()
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterGet get = SimpleStatusOuterGet.of(params, phaser);
        SimpleFutureGet future = holder.get(get);
        UpcsmReportSender ml = this.mlxact.get();
        return this.build(ml, future);
    }

    @Override
    public UpcsmReportSender del() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodDel[] params = new SimpleMethodDel[] {
            SimpleMethodDelParamsDefault.of()
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterDel del = SimpleStatusOuterDel.of(params, phaser);
        SimpleFutureDel future = holder.del(del);
        UpcsmReportSender ml = this.mlxact.del();
        return this.build(ml, future);
    }

    @Override
    public UpcsmReportSender pst(LogSequenceNumber lsn) //
        throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodPst[] params = new SimpleMethodPst[] {
            UpcsmParamsPstRecvLsn.of(lsn)
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(params, phaser);
        SimpleFuturePst future = holder.pst(pst);
        UpcsmReportSender ml = this.mlxact.pst(lsn);
        return this.build(ml, future);
    }

    private UpcsmReportSender build(UpcsmReportSender ml, SimpleFuture future)
        throws InterruptedException
    {
        PgResult snapshot = (PgResult)future.get().get(0);
        ImmutableList<PgResult> list = ImmutableList
            .<PgResult>builder() //
            .addAll(ml.snapshot) //
            .add(snapshot) //
            .build();
        return UpcsmReportSender.of(ml.mainline, list);
    }

    UpperRecordConsumer pollDefaultRecord(UpcsmActionRun action)
    {
        if (thread.isAlive()) {
            return null;
        }
        logger.error("snapshot post is not alive.");
        action.fetchThread = mlxact;
        SimpleStatus status = this.status.get();
        PgResult pgResult;
        if (status instanceof SimpleStatusInnerEnd) {
            SimpleStatusInnerEnd end = (SimpleStatusInnerEnd)status;
            pgResult = (PgResult) end.result.get(0);
        }
        else {
            pgResult = PgResultUnk.of(this.config);
        }
        mlxact.sslist.add(pgResult);
        return this.record;
    }
}
