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

    private final ConcurrentHashMap<String, UpperExesvc> repmap;

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
        UpperExesvcArgval config = UpperExesvcArgval.ofJsonObject(body);
        UpperExesvc exesvc = UpperExesvc.of(createts, name, config, this.keeper);

        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
            UpperExesvc status = this.repmap.putIfAbsent(name, exesvc);
            if (status != null) {
                UpperResult result = status.get(cmd);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UpperResult[]{ result });
            }
            UpperResult result = exesvc.put(cmd);
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
            UpperExesvc exesvc = this.repmap.remove(name);
            if (exesvc == null) {
                return ResponseEntity.notFound().build();
            }
            SimplePhaserOuter del = SimplePhaserOuter.of(4);
            UpperResult result = exesvc.del(del);
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
            UpperExesvc exesvc = this.repmap.get(name);
            if (exesvc == null) {
                return ResponseEntity.notFound().build();
            }
            SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
            UpperResult result = exesvc.get(cmd);
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
            UpperExesvc exesvc = this.repmap.get(name);
            if (exesvc == null) {
                return ResponseEntity.notFound().build();
            }
            SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
            UpperResult result = exesvc.pst(cmd, json);
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
            for (Map.Entry<String, UpperExesvc> entry : this.repmap.entrySet()) {
                final SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
                final UpperExesvc exesvc = entry.getValue();
                UpperResult r = exesvc.get(cmd);
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
            for (Map.Entry<String, UpperExesvc> entry : this.repmap.entrySet()) {
                SimplePhaserOuter cmd = SimplePhaserOuter.of(4);
                UpperExesvc exesvc = entry.getValue();
                exesvc.end(cmd);
            }
        }
        finally {
            lock.unlock();
        }
    }
}
