/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgmethod.BgMethodParamsDelDefault;
import com.hktcode.bgmethod.BgMethodParamsGetDefault;
import com.hktcode.bgmethod.BgMethodParamsPutDefault;
import com.hktcode.bgtriple.future.TriplePutBgFuture;
import com.hktcode.bgtriple.result.TripleDelBgResult;
import com.hktcode.bgtriple.result.TripleGetBgResult;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.bgtriple.status.TripleDelBgStatus;
import com.hktcode.bgtriple.status.TripleGetBgStatus;
import com.hktcode.bgtriple.status.TriplePutBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TripleBasicController //
    /* */< S extends TripleConsumer<S, P, D> //
    /* */, P extends TripleJunction<S, P, D> //
    /* */, D extends TripleProducer<S, P, D> //
    /* */, F
    /* */, L
    /* */, R
    /* */> //
{
    private static final Logger logger //
        = LoggerFactory.getLogger(TripleBasicController.class);

    protected final ConcurrentMap<String, TripleSwitcher<S, P, D>> quay //
        = new ConcurrentHashMap<>();

    protected TripleBasicController()
    {
    }

    @PutMapping //
        /* */( path = "/{name}" //
        ///* */, consumes = { MediaType.APPLICATION_JSON_VALUE //
        //* */ /*        */, MediaType.APPLICATION_JSON_UTF8_VALUE //
        ///* */ /*        */} //
        /* */)
    public ResponseEntity //
    put(@PathVariable("name") String name, @RequestBody JsonNode body) //
        throws Exception
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (body == null) {
            throw new ArgumentNullException("body");
        }
        if ("".equals(name)) {
            logger.info("can not put to /{name} with empty name.");
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
        BgMethodParamsPutDefault<S> c = BgMethodParamsPutDefault.of();
        BgMethodParamsPutDefault<P> j = BgMethodParamsPutDefault.of();
        BgMethodParamsPutDefault<D> p = BgMethodParamsPutDefault.of();
        TriplePutBgStatus<S, P, D> put = TriplePutBgStatus.of(c, j, p);
        AtomicReference<TripleBasicBgStatus<S, P, D>> ref = new AtomicReference<>(put);
        F conf = this.createConfig(body);
        BlockingQueue<L> comein = this.createComein(conf);
        BlockingQueue<R> getout = this.createGetout(conf);
        TripleConsumer<S, P, D> consumer = this.createConsumer(conf, ref, comein, getout);
        TripleJunction<S, P, D> junction = this.createJunction(conf, ref, comein, getout);
        TripleProducer<S, P, D> producer = this.createProducer(conf, ref, comein, getout);
        TripleSwitcher<S, P, D> temp = this.quay.putIfAbsent(name, consumer.switcher());
        if (temp != null) {
            // TODO: 确定body的表示形式
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Thread thread = new Thread(producer);
        thread.setName("triple-producer-" + name);
        thread.start();
        thread = new Thread(junction);
        thread.setName("triple-junction-" + name);
        thread.start();
        thread = new Thread(consumer);
        thread.setName("triple-consumer-" + name);
        thread.start();
        TriplePutBgFuture future = put.newFuture();
        return ResponseEntity.ok(future.get());
    }

    @DeleteMapping(path="/{name}")
    public ResponseEntity del(@PathVariable("name") String name) //
        throws Exception
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if ("".equals(name)) {
            logger.info("can not del from {name} with empty name.");
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
        TripleSwitcher<S, P, D> worker = this.quay.remove(name);
        if (worker == null) {
            logger.info("the worker does not exists: name={}", name);
            return ResponseEntity.notFound().build();
        }
        BgMethodParamsDelDefault<S> c = BgMethodParamsDelDefault.of();
        BgMethodParamsDelDefault<P> j = BgMethodParamsDelDefault.of();
        BgMethodParamsDelDefault<D> p = BgMethodParamsDelDefault.of();
        TripleDelBgStatus<S, P, D> del = TripleDelBgStatus.of(c, j, p);
        TripleDelBgResult<S, P, D> r = worker.del(del).newFuture().get();
        return ResponseEntity.ok(r);
    }

    @GetMapping(name="/")
    public ResponseEntity get() throws Exception
    {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping(path="/{name}")
    public ResponseEntity get(@PathVariable("name") String name) //
        throws Exception
    {
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if ("".equals(name)) {
            return this.get();
        }
        TripleSwitcher<S, P, D> worker = this.quay.get(name);
        if (worker == null) {
            logger.info("the worker does not exists: name={}", name);
            return ResponseEntity.notFound().build();
        }
        BgMethodParamsGetDefault<S> c = BgMethodParamsGetDefault.of();
        BgMethodParamsGetDefault<P> j = BgMethodParamsGetDefault.of();
        BgMethodParamsGetDefault<D> p = BgMethodParamsGetDefault.of();
        TripleGetBgStatus<S, P, D> get = TripleGetBgStatus.of(c, j, p);
        TripleGetBgResult<S, P, D> r = worker.get(get).newFuture().get();
        return ResponseEntity.ok(r);
    }

    protected abstract F createConfig(JsonNode jsonNode) throws Exception;

    protected abstract TripleConsumer<S, P, D> createConsumer //
        /* */( F config //
        /* */, AtomicReference<TripleBasicBgStatus<S, P, D>> status //
        /* */, BlockingQueue<L> comein //
        /* */, BlockingQueue<R> getout //
        /* */) throws Exception;

    protected abstract TripleJunction<S, P, D> createJunction //
        /* */( F config //
        /* */, AtomicReference<TripleBasicBgStatus<S, P, D>> status //
        /* */, BlockingQueue<L> comein //
        /* */, BlockingQueue<R> getout //
        /* */) throws Exception;

    protected abstract TripleProducer<S, P, D> createProducer //
        /* */( F config //
        /* */, AtomicReference<TripleBasicBgStatus<S, P, D>> status //
        /* */, BlockingQueue<L> comein //
        /* */, BlockingQueue<R> getout //
        /* */) throws Exception;

    protected abstract BlockingQueue<L> createComein(F config) //
        throws Exception;

    protected abstract BlockingQueue<R> createGetout(F config) //
        throws Exception;
}
