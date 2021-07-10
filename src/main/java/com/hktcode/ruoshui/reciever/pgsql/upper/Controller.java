/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.hktcode.jackson.exception.JsonSchemaValidationImplException;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.exception.RuoshuiNameFormatException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import static com.hktcode.ruoshui.reciever.pgsql.upper.Entity.Schema.SCHEMA;

@RestController("upperController")
@RequestMapping("api/recievers/upper")
public class Controller implements DisposableBean
{
    private static final MissingNode MISSING_NODE = MissingNode.getInstance();

    private static final Pattern NAME_PATTERN //
            = Pattern.compile("^[a-z][a-z_0-9]*(\\.[a-z][a-z_0-9]*)*$");

    private static final long NAME_MAXLENGTH = 128;

    private final JsonSchema schema;

    private final KeeperOnlyone keeper;

    private final ReadWriteLock locker = new ReentrantReadWriteLock();

    private final ConcurrentHashMap<String, Entity> repmap;

    private final ThreadPoolTaskExecutor exesvc;

    public Controller //
        /**/( @Autowired KeeperOnlyone dricab //
            , @Autowired ThreadPoolTaskExecutor exesvc //
            ) throws ProcessingException
    {
        this.keeper = dricab;
        this.exesvc = exesvc;
        this.repmap = new ConcurrentHashMap<>();
        this.schema = JsonSchemaFactory.byDefault().getJsonSchema(SCHEMA);
    }

    @PutMapping("{name}")
    public ResponseEntity<Entity.Result[]> //
    put(@PathVariable("name") String name, @RequestBody JsonNode body) //
            throws InterruptedException, ProcessingException, IOException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (body == null) {
            throw new ArgumentNullException("body");
        }
        if (name.length() > NAME_MAXLENGTH) {
            throw new RuoshuiNameFormatException(name);
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new RuoshuiNameFormatException(name);
        }
        ProcessingReport report = schema.validate(body);
        if (!report.isSuccess()) {
            throw new JsonSchemaValidationImplException(report);
        }
        Entity exesvc = Entity.of(name, body);

        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            Entity status = this.repmap.putIfAbsent(name, exesvc);
            if (status != null) {
                Entity.Result result = status.modify(Long.MAX_VALUE, MISSING_NODE, this.keeper::updertYml);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Entity.Result[]{ result });
            }
            this.exesvc.submit(exesvc.producer());
            this.exesvc.submit(exesvc.junction());
            this.exesvc.submit(exesvc.consumer());
            Entity.Result result = exesvc.modify(Long.MAX_VALUE, MISSING_NODE, this.keeper::updertYml);
            return ResponseEntity.ok(new Entity.Result[] { result });
        }
        finally {
            lock.unlock();
        }
    }

    @DeleteMapping("{name}")
    public ResponseEntity<Entity.Result[]> //
    del(@PathVariable("name") String name) //
            throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (name.length() > NAME_MAXLENGTH) {
            throw new RuoshuiNameFormatException(name);
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new RuoshuiNameFormatException(name);
        }
        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            Entity exesvc = this.repmap.remove(name);
            if (exesvc == null) {
                return ResponseEntity.notFound().build();
            }
            long finishts = System.currentTimeMillis();
            Entity.Result result = exesvc.modify(finishts, MISSING_NODE, this.keeper::deleteYml);
            return ResponseEntity.ok(new Entity.Result[]{result});
        }
        finally {
            lock.unlock();
        }
    }

    @GetMapping("{name}")
    public ResponseEntity<Entity.Result[]> //
    get(@PathVariable("name") String name) //
            throws InterruptedException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (name.length() > NAME_MAXLENGTH) {
            throw new RuoshuiNameFormatException(name);
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new RuoshuiNameFormatException(name);
        }
        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            Entity exesvc = this.repmap.get(name);
            if (exesvc == null) {
                return ResponseEntity.notFound().build();
            }
            long finishts = Long.MAX_VALUE;
            Entity.Result result = exesvc.modify(finishts, MISSING_NODE, this.keeper::updertYml);
            return ResponseEntity.ok(new Entity.Result[] { result });
        }
        finally {
            lock.unlock();
        }
    }

    @PostMapping("{name}")
    public ResponseEntity<Entity.Result[]> //
    pst(@PathVariable("name") String name, @RequestBody JsonNode body) //
            throws InterruptedException, ProcessingException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (body == null) {
            throw new ArgumentNullException("body");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new RuoshuiNameFormatException(name);
        }
        ProcessingReport report = schema.validate(body);
        if (!report.isSuccess()) {
            throw new JsonSchemaValidationImplException(report);
        }
        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            Entity exesvc = this.repmap.get(name);
            if (exesvc == null) {
                return ResponseEntity.notFound().build();
            }
            long finishts = Long.MAX_VALUE;
            Entity.Result result = exesvc.modify(finishts, body, this.keeper::updertYml);
            return ResponseEntity.ok(new Entity.Result[] { result });
        }
        finally {
            lock.unlock();
        }
    }

    @GetMapping
    public ResponseEntity<Entity.Result[]> get() //
            throws InterruptedException
    {
        Lock lock = this.locker.writeLock();
        lock.lock();
        try {
            Entity.Result[] result = new Entity.Result[this.repmap.size()];
            int index = 0;
            long finishts = Long.MAX_VALUE;
            for (Map.Entry<String, Entity> entry : this.repmap.entrySet()) {
                final Entity exesvc = entry.getValue();
                Entity.Result r = exesvc.modify(finishts, MissingNode.getInstance(), this.keeper::updertYml);
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
            for (Map.Entry<String, Entity> entry : this.repmap.entrySet()) {
                long finishts = System.currentTimeMillis();
                Entity exesvc = entry.getValue();
                exesvc.modify(finishts, MissingNode.getInstance(), this.keeper::updertYml);
            }
        }
        finally {
            lock.unlock();
        }
    }
}
