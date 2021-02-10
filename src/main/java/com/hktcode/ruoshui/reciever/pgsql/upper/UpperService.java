/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.simple.SimpleHolder;
import com.hktcode.simple.SimplePhaserOuter;
import com.hktcode.simple.Simple;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.storeman.UpperKeeperOnlyone;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class UpperService implements DisposableBean
{
    private final UpperKeeperOnlyone keeper;

    private final ReadWriteLock locker = new ReentrantReadWriteLock();

    private final ConcurrentHashMap<String, SimpleHolder<UpperEntity>> repmap;

    public UpperService(@Autowired UpperKeeperOnlyone dricab)
    {
        this.keeper = dricab;
        this.repmap = new ConcurrentHashMap<>();
    }

    public ResponseEntity<UpperResult[]> put(String name, ObjectNode body) //
            throws InterruptedException, IOException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (body == null) {
            throw new ArgumentNullException("body");
        }
        long createts = System.currentTimeMillis();
        UpperConfig config = UpperConfig.ofJsonObject(body);
        UpperEntity entity = UpperEntity.of(createts, name, config);
        SimpleHolder<UpperEntity> h = SimpleHolder.of(entity);

        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
            SimpleHolder<UpperEntity> status = this.repmap.putIfAbsent(name, h);
            if (status != null) {
                UpperResult result = status.run(cmd, UpperEntity::get);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UpperResult[]{ result });
            }
            Thread thread = new Thread(Simple.of(entity.producer, h));
            thread.setDaemon(false);
            thread.setName("ruoshui-upper-producer");
            thread.start();
            thread = new Thread(Simple.of(entity.junction, h));
            thread.setDaemon(false);
            thread.setName("ruoshui-upper-junction");
            thread.start();
            thread = new Thread(Simple.of(entity.consumer, h));
            thread.setDaemon(false);
            thread.setName("ruoshui-upper-consumer");
            thread.start();
            UpperResult result = h.run(cmd, this.keeper::put);
            return ResponseEntity.ok(new UpperResult[] { result });
        }
        finally {
            lock.unlock();
        }
    }

    public ResponseEntity<UpperResult[]> del(String name) throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            SimpleHolder<UpperEntity> holder = this.repmap.remove(name);
            if (holder == null) {
                return ResponseEntity.notFound().build();
            }
            SimplePhaserOuter del = SimplePhaserOuter.of(4);
            UpperResult result = holder.run(del, this.keeper::del);
            return ResponseEntity.ok(new UpperResult[]{result});
        }
        finally {
            lock.unlock();
        }
    }

    public ResponseEntity<UpperResult[]> get(String name) throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            SimpleHolder<UpperEntity> holder = this.repmap.remove(name);
            if (holder == null) {
                return ResponseEntity.notFound().build();
            }
            SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
            UpperResult result = holder.run(cmd, UpperEntity::get);
            return ResponseEntity.ok(new UpperResult[] { result });
        }
        finally {
            lock.unlock();
        }
    }

    public ResponseEntity<UpperResult[]> pst(String name, JsonNode json)
            throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            SimpleHolder<UpperEntity> status = this.repmap.get(name);
            if (status == null) {
                return ResponseEntity.notFound().build();
            }
            SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
            UpperResult result = status.run(cmd, (e, d)->this.keeper.pst(e, json));
            return ResponseEntity.ok(new UpperResult[] { result });
        }
        finally {
            lock.unlock();
        }
    }

    public ResponseEntity<UpperResult[]> get() throws InterruptedException
    {
        Lock lock = this.locker.writeLock();
        lock.lock();
        try {
            UpperResult[] result = new UpperResult[this.repmap.size()];
            int index = 0;
            for (Map.Entry<String, SimpleHolder<UpperEntity>> entry : this.repmap.entrySet()) {
                SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
                UpperResult r = entry.getValue().run(cmd, UpperEntity::get);
                result[index++] = r;
            }
            return ResponseEntity.ok(result);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void destroy() throws InterruptedException
    {
        Lock lock = this.locker.writeLock();
        lock.lock();
        try {
            for (Map.Entry<String, SimpleHolder<UpperEntity>> entry : this.repmap.entrySet()) {
                SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
                entry.getValue().cmd(cmd, UpperEntity::end);
            }
        }
        finally {
            lock.unlock();
        }
    }
}
