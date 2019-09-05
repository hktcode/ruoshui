/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.future.SimpleFuturePut;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConfig;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.UpperSnapshotPstParams;
import com.hktcode.pgstack.ruoshui.upper.consumer.Upcsm;
import com.hktcode.pgstack.ruoshui.upper.junction.Upjct;
import com.hktcode.pgstack.ruoshui.upper.producer.Uppdc;
import com.hktcode.pgstack.ruoshui.upper.producer.UpperProducerRecord;
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
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleFuturePut future = holder.put(config);
        ImmutableList<SimpleMethodAllResult> list = future.get();
        return ResponseEntity.ok(list);
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
