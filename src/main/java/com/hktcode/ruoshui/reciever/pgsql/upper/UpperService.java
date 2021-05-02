/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.simple.SimplePhaserOuter;
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

    private final ConcurrentHashMap<String, UpperExesvr> repmap;

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
        UpperExesvr holder = UpperExesvr.of(createts, name, config, this.keeper);

        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
            UpperExesvr status = this.repmap.putIfAbsent(name, holder);
            if (status != null) {
                UpperResult result = status.get(cmd);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UpperResult[]{ result });
            }
            UpperResult result = holder.put(cmd);
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
            UpperExesvr holder = this.repmap.remove(name);
            if (holder == null) {
                return ResponseEntity.notFound().build();
            }
            SimplePhaserOuter del = SimplePhaserOuter.of(4);
            UpperResult result = holder.del(del);
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
            UpperExesvr holder = this.repmap.get(name);
            if (holder == null) {
                return ResponseEntity.notFound().build();
            }
            SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
            UpperResult result = holder.get(cmd);
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
            UpperExesvr holder = this.repmap.get(name);
            if (holder == null) {
                return ResponseEntity.notFound().build();
            }
            SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
            UpperResult result = holder.pst(cmd, json);
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
            for (Map.Entry<String, UpperExesvr> entry : this.repmap.entrySet()) {
                final SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
                final UpperExesvr holder = entry.getValue();
                UpperResult r = holder.get(cmd);
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
            for (Map.Entry<String, UpperExesvr> entry : this.repmap.entrySet()) {
                SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
                UpperExesvr holder = entry.getValue();
                holder.end(cmd);
            }
        }
        finally {
            lock.unlock();
        }
    }
}
