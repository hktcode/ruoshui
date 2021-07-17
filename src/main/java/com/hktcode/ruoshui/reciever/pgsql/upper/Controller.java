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
import com.hktcode.simple.SimpleLockedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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

import static com.hktcode.ruoshui.reciever.pgsql.upper.Entity.Result;
import static com.hktcode.ruoshui.reciever.pgsql.upper.Entity.Schema.SCHEMA;
import static java.lang.System.currentTimeMillis;

@RestController("upperController")
@RequestMapping("api/recievers/upper")
public class Controller implements DisposableBean
{
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
    public ResponseEntity<Result[]> //
    put(@PathVariable("name") String name, @RequestBody JsonNode body) //
            throws InterruptedException, ProcessingException, IOException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (body == null) {
            throw new ArgumentNullException("body");
        }
        check(name, body);
        Entity newval = Entity.of(name, body);

        long finish = Long.MAX_VALUE;
        body = MissingNode.getInstance();
        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            HttpStatus status = HttpStatus.FORBIDDEN;
            Entity entity = this.repmap.putIfAbsent(name, newval);
            if (entity == null) {
                this.exesvc.submit(newval.producer());
                this.exesvc.submit(newval.junction());
                this.exesvc.submit(newval.consumer());
                entity = newval;
                status = HttpStatus.OK;
            }
            Result result = entity.modify(finish, body, keeper::updertYml);
            return ResponseEntity.status(status).body(new Result[]{ result });
        }
        finally {
            lock.unlock();
        }
    }

    @DeleteMapping("{name}")
    public ResponseEntity<Result[]> del(@PathVariable("name") String name) //
            throws InterruptedException, ProcessingException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        JsonNode body = MissingNode.getInstance();
        check(name, body);
        return this.pst(HttpMethod.DELETE, name, body);
    }

    @GetMapping("{name}")
    public ResponseEntity<Result[]> get(@PathVariable("name") String name) //
            throws InterruptedException, ProcessingException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        JsonNode body = MissingNode.getInstance();
        check(name, body);
        return this.pst(HttpMethod.GET, name, body);
    }

    @PostMapping("{name}")
    public ResponseEntity<Result[]> //
    pst(@PathVariable("name") String name, @RequestBody JsonNode body) //
            throws InterruptedException, ProcessingException
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (body == null) {
            throw new ArgumentNullException("body");
        }
        check(name, body);
        return this.pst(HttpMethod.POST, name, body);
    }

    @GetMapping
    public ResponseEntity<Result[]> get() throws InterruptedException
    {
        long finishts = Long.MAX_VALUE;
        JsonNode body = MissingNode.getInstance();
        Lock lock = this.locker.writeLock();
        lock.lock();
        try {
            Result[] result = new Result[this.repmap.size()];
            int index = 0;
            for (Map.Entry<String, Entity> entry : this.repmap.entrySet()) {
                final Entity exesvc = entry.getValue();
                do {
                    try {
                        Result r = exesvc.modify(finishts, body, keeper::updertYml);
                        result[index++] = r;
                        break;
                    }
                    catch (SimpleLockedException ignored) {
                    }
                } while (true);
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
            JsonNode body = MissingNode.getInstance();
            for (Map.Entry<String, Entity> entry : this.repmap.entrySet()) {
                long finishts = currentTimeMillis();
                Entity exesvc = entry.getValue();
                do {
                    try {
                        exesvc.modify(finishts, body, keeper::updertYml);
                        break;
                    }
                    catch (SimpleLockedException ignored) {
                    }
                } while (true);
            }
        }
        finally {
            lock.unlock();
        }
    }

    private void check(String name, JsonNode body) throws ProcessingException
    {
        if (name.length() > NAME_MAXLENGTH) {
            throw new RuoshuiNameFormatException(name);
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new RuoshuiNameFormatException(name);
        }
        if (body.isMissingNode()) {
            return;
        }
        ProcessingReport r = schema.validate(body);
        if (r.isSuccess()) {
            return;
        }
        throw new JsonSchemaValidationImplException(r);
    }

    private ResponseEntity<Result[]> //
    pst(HttpMethod method, String name, JsonNode body) //
            throws InterruptedException
    {
        Lock lock = this.locker.readLock();
        lock.lock();
        try {
            Entity entity;
            long finish;
            Entity.Keeper keeper;
            if (method == HttpMethod.DELETE) {
                finish = currentTimeMillis();
                keeper = this.keeper::deleteYml;
                entity = this.repmap.remove(name);
            } else {
                finish = Long.MAX_VALUE;
                keeper = this.keeper::updertYml;
                entity = this.repmap.get(name);
            }
            if (entity == null) {
                return ResponseEntity.notFound().build();
            }
            Result result = entity.modify(finish, body, keeper);
            return ResponseEntity.ok(new Result[] { result });
        }
        finally {
            lock.unlock();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
}
