/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.storeman.UpperKeeperOnlyone;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    private final ConcurrentHashMap<String, UpperHolder> repmap;

    private final ThreadPoolTaskExecutor exesvc;

    public UpperService(@Autowired UpperKeeperOnlyone dricab, @Autowired ThreadPoolTaskExecutor exesvc)
    {
        this.keeper = dricab;
        this.exesvc = exesvc;
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
        UpperHolderArgval argval = UpperHolderArgval.ofJsonObject(name, body);
        UpperHolder exesvc = UpperHolder.of(argval);

        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            UpperHolder status = this.repmap.putIfAbsent(name, exesvc);
            if (status != null) {
                UpperResult result = status.modify(Long.MAX_VALUE, MissingNode.getInstance(), this.keeper::updertYml);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UpperResult[]{ result });
            }
            this.exesvc.submit(exesvc.producer());
            this.exesvc.submit(exesvc.junction());
            this.exesvc.submit(exesvc.consumer());
            UpperResult result = exesvc.modify(Long.MAX_VALUE, MissingNode.getInstance(), this.keeper::updertYml);
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
            UpperHolder exesvc = this.repmap.remove(name);
            if (exesvc == null) {
                return ResponseEntity.notFound().build();
            }
            long finishts = System.currentTimeMillis();
            UpperResult result = exesvc.modify(finishts, MissingNode.getInstance(), this.keeper::deleteYml);
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
            UpperHolder exesvc = this.repmap.get(name);
            if (exesvc == null) {
                return ResponseEntity.notFound().build();
            }
            long finishts = Long.MAX_VALUE;
            UpperResult result = exesvc.modify(finishts, MissingNode.getInstance(), this.keeper::updertYml);
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
            UpperHolder exesvc = this.repmap.get(name);
            if (exesvc == null) {
                return ResponseEntity.notFound().build();
            }
            long finishts = Long.MAX_VALUE;
            UpperResult result = exesvc.modify(finishts, json, this.keeper::updertYml);
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
            long finishts = Long.MAX_VALUE;
            for (Map.Entry<String, UpperHolder> entry : this.repmap.entrySet()) {
                final UpperHolder exesvc = entry.getValue();
                UpperResult r = exesvc.modify(finishts, MissingNode.getInstance(), this.keeper::updertYml);
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
            for (Map.Entry<String, UpperHolder> entry : this.repmap.entrySet()) {
                long finishts = System.currentTimeMillis();
                UpperHolder exesvc = entry.getValue();
                exesvc.modify(finishts, MissingNode.getInstance(), this.keeper::updertYml);
            }
        }
        finally {
            lock.unlock();
        }
    }
}
