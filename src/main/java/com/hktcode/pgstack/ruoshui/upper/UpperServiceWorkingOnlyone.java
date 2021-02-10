/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.Upcsm;
import com.hktcode.pgstack.ruoshui.upper.junction.Upjct;
import com.hktcode.pgstack.ruoshui.upper.producer.Uppdc;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;

public class UpperServiceWorkingOnlyone implements UpperServiceWorking
{
    public static UpperServiceWorkingOnlyone of()
    {
        return new UpperServiceWorkingOnlyone();
    }

    private final SimpleHolder status;

    private UpperServiceWorkingOnlyone()
    {
        SimpleMethod[] method = new SimpleMethod[] {
            SimpleMethodParamsPutDefault.of(),
            SimpleMethodParamsPutDefault.of(),
            SimpleMethodParamsPutDefault.of()
        };
        SimpleStatusOuter put = SimpleStatusOuter.of(new Phaser(4), method);
        this.status = SimpleHolder.of(put);
    }

    @Override
    public ResponseEntity<?> put(UpperConfig config) //
        throws InterruptedException
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
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
        ImmutableList<? extends SimpleMethodResult> list = this.status.run();
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<?> del() throws InterruptedException
    {
        SimpleMethod[] method = new SimpleMethod[] {
            SimpleMethodParamsDelDefault.of(),
            SimpleMethodParamsDelDefault.of(),
            SimpleMethodParamsDelDefault.of()
        };
        SimpleStatusOuter del = SimpleStatusOuter.of(new Phaser(4), method);
        ImmutableList<SimpleMethodResult> list = this.status.run(del);
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<?> get() throws InterruptedException
    {
        SimpleMethod[] method = new SimpleMethod[] {
            SimpleMethodParamsGetDefault.of(),
            SimpleMethodParamsGetDefault.of(),
            SimpleMethodParamsGetDefault.of()
        };
        SimpleStatusOuter get = SimpleStatusOuter.of(new Phaser(4), method);
        ImmutableList<SimpleMethodResult> list = this.status.run(get);
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<?> pst(JsonNode json)
        throws InterruptedException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        SimpleMethod[] method = new SimpleMethod[] {
            SimpleMethodParamsPstDefault.of(),
            SimpleMethodParamsPstDefault.of(),
            SimpleMethodParamsPstDefault.of()
        };
        SimpleStatusOuter pst = SimpleStatusOuter.of(new Phaser(4), method);
        ImmutableList<SimpleMethodResult> list = this.status.run(pst);
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
