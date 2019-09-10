/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.future.SimpleFutureDel;
import com.hktcode.bgsimple.future.SimpleFutureGet;
import com.hktcode.bgsimple.future.SimpleFuturePst;
import com.hktcode.bgsimple.future.SimpleFuturePut;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpperSnapshotPstParams;
import com.hktcode.pgstack.ruoshui.upper.consumer.Upcsm;
import com.hktcode.pgstack.ruoshui.upper.junction.Upjct;
import com.hktcode.pgstack.ruoshui.upper.producer.Uppdc;
import org.springframework.http.ResponseEntity;

import javax.script.ScriptException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;

public class UpperServiceWorkingOnlyone implements UpperServiceWorking
{
    public static UpperServiceWorkingOnlyone of()
    {
        return new UpperServiceWorkingOnlyone();
    }

    private final AtomicReference<SimpleStatus> status;

    private UpperServiceWorkingOnlyone()
    {
        SimpleMethodPut[] method = new SimpleMethodPut[] {
            SimpleMethodPutParamsDefault.of(),
            SimpleMethodPutParamsDefault.of(),
            SimpleMethodPutParamsDefault.of()
        };
        SimpleStatus put = SimpleStatusOuterPut.of(method, new Phaser(4));
        this.status = new AtomicReference<>(put);
    }

    @Override
    public ResponseEntity put(UpperConfig config) //
        throws InterruptedException
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleFuturePut future = holder.put();
        BlockingQueue<UpperRecordConsumer> comein = new ArrayBlockingQueue<>(config.junction.comeinCount);
        BlockingQueue<UpperRecordProducer> getout = new ArrayBlockingQueue<>(config.junction.getoutCount);

        Upcsm consumer = Upcsm.of(config.consumer, this.status, comein);
        Upjct junction = Upjct.of(config.junction, comein, getout, this.status);
        Uppdc producer = Uppdc.of(config.producer, getout, status);
        Thread thread = new Thread(producer);
        thread.setDaemon(false);
        thread.setName("ruoshui-upper-producer");
        thread.start();
        thread = new Thread(junction);
        thread.setDaemon(false);
        thread.setName("ruoshui-upper-junction");
        thread.start();
        thread = new Thread(consumer);
        thread.setDaemon(false);
        thread.setName("ruoshui-upper-consumer");
        thread.start();
        ImmutableList<SimpleMethodAllResult> list = future.get();
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity del() throws InterruptedException
    {
        SimpleMethodDel[] method = new SimpleMethodDel[] {
            SimpleMethodDelParamsDefault.of(),
            SimpleMethodDelParamsDefault.of(),
            SimpleMethodDelParamsDefault.of()
        };
        SimpleStatusOuterDel del = SimpleStatusOuterDel.of(method, new Phaser(4));
        SimpleHolder holder = SimpleHolder.of(this.status);
        SimpleFutureDel future = holder.del(del);
        ImmutableList<SimpleMethodAllResult> list = future.get();
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity get() throws InterruptedException
    {
        SimpleMethodGet[] method = new SimpleMethodGet[] {
            SimpleMethodGetParamsDefault.of(),
            SimpleMethodGetParamsDefault.of(),
            SimpleMethodGetParamsDefault.of()
        };
        SimpleStatusOuterGet get = SimpleStatusOuterGet.of(method, new Phaser(4));
        SimpleHolder holder = SimpleHolder.of(this.status);
        SimpleFutureGet future = holder.get(get);
        ImmutableList<SimpleMethodAllResult> list = future.get();
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity pst(JsonNode json)
        throws InterruptedException, ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        SimpleMethodPst[] method = new SimpleMethodPst[] {
            UpperSnapshotPstParams.of(json),
            SimpleMethodPstParamsDefault.of(),
            SimpleMethodPstParamsDefault.of()
        };
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(method, new Phaser(4));
        SimpleHolder holder = SimpleHolder.of(this.status);
        SimpleFuturePst future = holder.pst(pst);
        ImmutableList<SimpleMethodAllResult> list = future.get();
        return ResponseEntity.ok(list);
    }

    @Override
    public UpperServiceWorking putService()
    {
        return this;
    }

    @Override
    public UpperServiceWaitingOnlyone delService()
    {
        return UpperServiceWaitingOnlyone.of();
    }
}