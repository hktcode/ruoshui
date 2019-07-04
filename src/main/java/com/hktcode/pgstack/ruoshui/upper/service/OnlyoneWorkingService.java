/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgmethod.BgMethodDelParamsDefault;
import com.hktcode.bgmethod.BgMethodGetParamsDefault;
import com.hktcode.bgmethod.BgMethodPstParamsDefault;
import com.hktcode.bgmethod.BgMethodPutParamsDefault;
import com.hktcode.bgtriple.TripleSwitcher;
import com.hktcode.bgtriple.result.TriplePutBgResult;
import com.hktcode.bgtriple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConfig;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperProducerRecord;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperSnapshotPstParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.script.ScriptException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class OnlyoneWorkingService implements WorkingService
{
    public static OnlyoneWorkingService of()
    {
        return new OnlyoneWorkingService();
    }

    private final AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status;

    private OnlyoneWorkingService()
    {
        BgMethodPutParamsDefault<UpperConsumer> c = BgMethodPutParamsDefault.of();
        BgMethodPutParamsDefault<UpperJunction> j = BgMethodPutParamsDefault.of();
        BgMethodPutParamsDefault<UpperProducer> p = BgMethodPutParamsDefault.of();
        TriplePutBgStatus<UpperConsumer, UpperJunction, UpperProducer> put = TriplePutBgStatus.of(c, j, p);
        this.status = new AtomicReference<>(put);
    }

    @Override
    public ResponseEntity put(UpperConfig config) //
        throws ExecutionException, InterruptedException
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer> s = this.status.get();
        if (!(s instanceof TriplePutBgStatus)) {
            // TODO: 抛出异常可能会好点.
            // TODO: 如何确保start只会被调用一次呢？
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        TriplePutBgStatus<UpperConsumer, UpperJunction, UpperProducer> put = (TriplePutBgStatus<UpperConsumer, UpperJunction, UpperProducer>) s;
        BlockingQueue<UpperConsumerRecord> comein = new ArrayBlockingQueue<>(config.junction.comeinCount);
        BlockingQueue<UpperProducerRecord> getout = new ArrayBlockingQueue<>(config.junction.getoutCount);

        UpperConsumer consumer = UpperConsumer.of(config.consumer, this.status, comein);
        UpperJunction junction = UpperJunction.of(config.junction, this.status, comein, getout);
        UpperProducer producer = UpperProducer.of(config.producer, this.status, getout);
        Thread thread = new Thread(producer);
        thread.setName("ruoshui-upper-producer");
        thread.start();
        thread = new Thread(junction);
        thread.setName("ruoshui-upper-junction");
        thread.start();
        thread = new Thread(consumer);
        thread.setName("ruoshui-upper-consumer");
        thread.start();
        TriplePutBgResult<UpperConsumer, UpperJunction, UpperProducer> result = put.newFuture().get();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity del() throws ExecutionException, InterruptedException
    {
        BgMethodDelParamsDefault<UpperConsumer> c = BgMethodDelParamsDefault.of();
        BgMethodDelParamsDefault<UpperJunction> j = BgMethodDelParamsDefault.of();
        BgMethodDelParamsDefault<UpperProducer> p = BgMethodDelParamsDefault.of();
        TripleDelBgStatus<UpperConsumer, UpperJunction, UpperProducer> del //
            = TripleDelBgStatus.of(c, j, p);
        TripleSwitcher<UpperConsumer, UpperJunction, UpperProducer> switcher //
            = TripleSwitcher.of(this.status);
        return ResponseEntity.ok(switcher.del(del).newFuture().get());
    }

    @Override
    public ResponseEntity get() throws ExecutionException, InterruptedException
    {
        BgMethodGetParamsDefault<UpperConsumer> c = BgMethodGetParamsDefault.of();
        BgMethodGetParamsDefault<UpperJunction> j = BgMethodGetParamsDefault.of();
        BgMethodGetParamsDefault<UpperProducer> p = BgMethodGetParamsDefault.of();
        TripleGetBgStatus<UpperConsumer, UpperJunction, UpperProducer> get //
            = TripleGetBgStatus.of(c, j, p);
        TripleSwitcher<UpperConsumer, UpperJunction, UpperProducer> switcher //
            = TripleSwitcher.of(this.status);
        return ResponseEntity.ok(switcher.get(get).newFuture().get());
    }

    @Override
    public ResponseEntity pst(JsonNode json)
        throws ExecutionException, InterruptedException, ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        UpperSnapshotPstParams c = UpperSnapshotPstParams.of(json);
        BgMethodPstParamsDefault<UpperJunction> j = BgMethodPstParamsDefault.of();
        BgMethodPstParamsDefault<UpperProducer> p = BgMethodPstParamsDefault.of();
        TriplePstBgStatus<UpperConsumer, UpperJunction, UpperProducer> pst //
            = TriplePstBgStatus.of(c, j, p);
        TripleSwitcher<UpperConsumer, UpperJunction, UpperProducer> switcher //
            = TripleSwitcher.of(this.status);
        return ResponseEntity.ok(switcher.pst(pst).newFuture().get());
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
