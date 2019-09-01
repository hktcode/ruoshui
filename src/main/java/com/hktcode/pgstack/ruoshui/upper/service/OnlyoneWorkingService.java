/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.junction.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.producer.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperConfig;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.producer.UpperProducerRecord;
import com.hktcode.pgstack.ruoshui.upper.UpperSnapshotPstParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.script.ScriptException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;

public class OnlyoneWorkingService implements WorkingService
{
    public static OnlyoneWorkingService of()
    {
        return new OnlyoneWorkingService();
    }

    private final AtomicReference<SimpleStatus> status;

    private OnlyoneWorkingService()
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
        throws ExecutionException, InterruptedException
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        SimpleStatus s = this.status.get();
        if (!(s instanceof SimpleStatusOuterPut)) {
            // TODO: 抛出异常可能会好点.
            // TODO: 如何确保start只会被调用一次呢？
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        SimpleStatusOuterPut put = (SimpleStatusOuterPut)s;
        SimpleHolder holder = SimpleHolder.of(status);
        BlockingQueue<UpperConsumerRecord> comein = new ArrayBlockingQueue<>(config.junction.comeinCount);
        BlockingQueue<UpperProducerRecord> getout = new ArrayBlockingQueue<>(config.junction.getoutCount);

        UpperConsumer consumer = UpperConsumer.of(config.consumer, this.status, comein);
        UpperJunction junction = UpperJunction.of(config.junction, this.status, comein, getout);
        UpperProducer producer = UpperProducer.of(config.producer, this.status, getout);
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
        put.phaser.awaitAdvanceInterruptibly(put.phaser.arrive());
        // TODO:
        SimpleStatusInnerRun run = SimpleStatusInnerRun.of();
        this.status.compareAndSet(put, run);
        put.phaser.arriveAndDeregister();
        return ResponseEntity.ok().build(); // TODO:
    }

    @Override
    public ResponseEntity del() throws ExecutionException, InterruptedException
    {
        SimpleMethodDel[] method = new SimpleMethodDel[] {
            SimpleMethodDelParamsDefault.of(),
            SimpleMethodDelParamsDefault.of(),
            SimpleMethodDelParamsDefault.of()
        };
        SimpleStatusOuterDel del = SimpleStatusOuterDel.of(method, new Phaser(3));
        SimpleHolder holder = SimpleHolder.of(this.status);
        holder.del(del);
        return ResponseEntity.ok().build(); // TODO:
    }

    @Override
    public ResponseEntity get() throws ExecutionException, InterruptedException
    {
        SimpleMethodGet[] method = new SimpleMethodGet[] {
            SimpleMethodGetParamsDefault.of(),
            SimpleMethodGetParamsDefault.of(),
            SimpleMethodGetParamsDefault.of()
        };
        SimpleStatusOuterGet get = SimpleStatusOuterGet.of(method, new Phaser(3));
        SimpleHolder holder = SimpleHolder.of(this.status);
        holder.get(get);
        return ResponseEntity.ok().build(); // TODO:
    }

    @Override
    public ResponseEntity pst(JsonNode json)
        throws ExecutionException, InterruptedException, ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        SimpleMethodPst[] method = new SimpleMethodPst[] {
            UpperSnapshotPstParams.of(json),
            SimpleMethodPstParamsDefault.of(),
            SimpleMethodPstParamsDefault.of()
        };
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(method, new Phaser(3));
        SimpleHolder holder = SimpleHolder.of(this.status);
        holder.pst(pst);
        return ResponseEntity.ok().build(); // TODO:
    }

    @Override
    public WorkingService putService()
    {
        return this;
    }

    @Override
    public OnlyoneWaitingService delService()
    {
        return OnlyoneWaitingService.of();
    }
}
