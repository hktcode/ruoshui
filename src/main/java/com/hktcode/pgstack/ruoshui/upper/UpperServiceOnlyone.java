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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;

public class UpperServiceOnlyone implements UpperService
{
    public static UpperServiceOnlyone of()
    {
        return new UpperServiceOnlyone();
    }

    private final ConcurrentHashMap<String, SimpleHolder> holder;

    private UpperServiceOnlyone()
    {
        this.holder = new ConcurrentHashMap<>();
    }

    @Override
    public ResponseEntity<?> put(String name, UpperConfig config) //
        throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        BlockingQueue<UpperRecordConsumer> comein = new ArrayBlockingQueue<>(config.junction.comeinCount);
        BlockingQueue<UpperRecordProducer> getout = new ArrayBlockingQueue<>(config.junction.getoutCount);

        SimpleHolder s = SimpleHolder.of();
        Upcsm consumer = Upcsm.of(config.consumer, s, comein);
        Upjct junction = Upjct.of(config.junction, comein, getout, s);
        Uppdc producer = Uppdc.of(config.producer, getout, s);

        SimpleHolder status = this.holder.putIfAbsent(name, s);
        if (status == null) {
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
            status = s;
        }
        SimpleMethod[] method = new SimpleMethod[] {
                SimpleMethodParamsPutDefault.of(),
                SimpleMethodParamsPutDefault.of(),
                SimpleMethodParamsPutDefault.of()
        };
        SimpleStatusCmd put = SimpleStatusCmd.of(new Phaser(3), method);
        ImmutableList<? extends SimpleMethodResult> list = status.cmd(put);
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<?> del(String name) throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        SimpleHolder status = this.holder.remove(name);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        SimpleMethod[] method = new SimpleMethod[] {
            SimpleMethodParamsDelDefault.of(),
            SimpleMethodParamsDelDefault.of(),
            SimpleMethodParamsDelDefault.of()
        };
        SimpleStatusCmd del = SimpleStatusCmd.of(new Phaser(3), method);
        ImmutableList<SimpleMethodResult> list = status.cmd(del);
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<?> get(String name) throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        SimpleHolder status = this.holder.get(name);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        SimpleMethod[] method = new SimpleMethod[] {
            SimpleMethodParamsGetDefault.of(),
            SimpleMethodParamsGetDefault.of(),
            SimpleMethodParamsGetDefault.of()
        };
        SimpleStatusCmd get = SimpleStatusCmd.of(new Phaser(3), method);
        ImmutableList<SimpleMethodResult> list = status.cmd(get);
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<?> pst(String name, JsonNode json)
        throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        SimpleHolder status = this.holder.get(name);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        SimpleMethod[] method = new SimpleMethod[] {
            SimpleMethodParamsPstDefault.of(),
            SimpleMethodParamsPstDefault.of(),
            SimpleMethodParamsPstDefault.of()
        };
        SimpleStatusCmd pst = SimpleStatusCmd.of(new Phaser(3), method);
        ImmutableList<SimpleMethodResult> list = status.cmd(pst);
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<?> get() throws InterruptedException
    {
        Map<String, ImmutableList<SimpleMethodResult>> result = new HashMap<>();
        for (Map.Entry<String, SimpleHolder> entry: this.holder.entrySet()) {
            SimpleMethod[] method = new SimpleMethod[] {
                    SimpleMethodParamsGetDefault.of(),
                    SimpleMethodParamsGetDefault.of(),
                    SimpleMethodParamsGetDefault.of()
            };
            SimpleStatusCmd get = SimpleStatusCmd.of(new Phaser(3), method);
            ImmutableList<SimpleMethodResult> list = entry.getValue().cmd(get);
            result.put(entry.getKey(), list);
        }
        return ResponseEntity.ok(result);
    }

    @Override
    public void destroy() throws InterruptedException
    {
        Enumeration<String> enumeration;
        while ((enumeration = this.holder.keys()).hasMoreElements()) {
            String key = enumeration.nextElement();
            this.del(key);
        }
    }
}
