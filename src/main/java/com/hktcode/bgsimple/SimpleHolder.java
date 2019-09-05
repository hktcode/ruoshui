/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple;

import com.hktcode.bgsimple.future.SimpleFutureDel;
import com.hktcode.bgsimple.future.SimpleFutureGet;
import com.hktcode.bgsimple.future.SimpleFuturePst;
import com.hktcode.bgsimple.future.SimpleFuturePut;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConfig;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.consumer.Upcsm;
import com.hktcode.pgstack.ruoshui.upper.junction.Upjct;
import com.hktcode.pgstack.ruoshui.upper.producer.Uppdc;
import com.hktcode.pgstack.ruoshui.upper.producer.UpperProducerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleHolder
{
    public static SimpleHolder of(AtomicReference<SimpleStatus> status) //
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new SimpleHolder(status);
    }

    private final AtomicReference<SimpleStatus> status;

    private SimpleHolder(AtomicReference<SimpleStatus> status)
    {
        this.status = status;
    }

    public SimpleFuturePut put(UpperConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        SimpleStatus s = this.status.get();
        if (!(s instanceof SimpleStatusOuterPut)) {
            // TODO: 抛出异常可能会好点.
            // TODO: 如何确保start只会被调用一次呢？
            // return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            throw new RuntimeException(); // TODO:
        }
        SimpleStatusOuterPut put = (SimpleStatusOuterPut)s;
        BlockingQueue<UpperConsumerRecord> comein = new ArrayBlockingQueue<>(config.junction.comeinCount);
        BlockingQueue<UpperProducerRecord> getout = new ArrayBlockingQueue<>(config.junction.getoutCount);

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
        return SimpleFuturePut.of(status, put);
    }

    public SimpleFuturePst pst(SimpleStatusOuterPst pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 判断pst中的bgMethod不是bgResult.
        SimpleStatus origin;
        SimpleStatus future;
        do {
            origin = this.status.get();
            future = origin.pst(pst);
        } while (/*  */!(future instanceof SimpleStatusOuterPst) //
                /**/|| future == origin  //
                /**/|| (    future == pst  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return SimpleFuturePst.of(status, (SimpleStatusOuterPst)future);
    }

    public SimpleFutureGet get(SimpleStatusOuterGet get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 判断get中的bgMethod不是bgResult.
        SimpleStatus origin;
        SimpleStatus future;
        do {
            origin = this.status.get();
            future = origin.get(get);
        } while (/*  */!(future instanceof SimpleStatusOuterGet) //
                /**/|| future != origin  //
                /**/|| (    future == get  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return SimpleFutureGet.of(status, (SimpleStatusOuterGet)future);
    }

    public SimpleFutureDel del(SimpleStatusOuterDel del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 判断del中的bgMethod不是bgResult.
        SimpleStatus origin;
        SimpleStatus future;
        do {
            origin = this.status.get();
            future = origin.del(del);
        } while (/*  */!(future instanceof SimpleStatusOuterDel)  //
                /**/|| future == origin  //
                /**/|| (    future != del //
                /*     */|| !this.status.compareAndSet(origin, future) //
                /*   */) //
            /**/);
        return SimpleFutureDel.of(status, (SimpleStatusOuterDel)future);
    }
}
