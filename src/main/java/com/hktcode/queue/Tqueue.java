package com.hktcode.queue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Tqueue<E> implements JacksonObject
{
    public static <E> Tqueue<E> of(TqueueArgval argval, TqueueGauges metric) //
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        LinkedBlockingQueue<E> tqueue = new LinkedBlockingQueue<>(argval.maxCapacity);
        return new Tqueue<>(tqueue, argval, metric);
    }

    private static final Logger logger = LoggerFactory.getLogger(Tqueue.class);

    private BlockingQueue<E> tqueue;
    public final TqueueArgval argval;
    public final TqueueGauges metric;

    private Tqueue(BlockingQueue<E> tqueue, TqueueArgval argval, TqueueGauges metric)
    {
        this.tqueue = tqueue;
        this.argval = argval;
        this.metric = metric;
    }

    public int size()
    {
        return this.tqueue.size();
    }

    // public resize(int newsize);
    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        long maxCapacity = this.argval.maxCapacity;
        this.argval.pst(node);
        // FIXME: 此处有BUG，不能让别人无限调小
        if (maxCapacity == this.argval.maxCapacity) {
            return;
        }
        LinkedBlockingQueue<E> tqueue = new LinkedBlockingQueue<>(this.argval.maxCapacity);
        this.tqueue.drainTo(tqueue);
        this.tqueue = tqueue;
    }

    public E poll() throws InterruptedException
    {
        long waitTimeout = argval.waitTimeout;
        long logDuration = argval.logDuration;
        long startsMillis = System.currentTimeMillis();
        E record = this.tqueue.poll(waitTimeout, TimeUnit.MILLISECONDS);
        long finishMillis = System.currentTimeMillis();
        metric.fetchMillis += (finishMillis - startsMillis);
        ++metric.fetchCounts;
        if (record != null) {
            ++metric.fetchRecord;
        }
        else if (finishMillis - metric.fetchLogger >= logDuration) {
            logger.info("tqueue.poll timeout: waitTimeout={}, logDuration={}" //
                    , waitTimeout, logDuration);
            metric.fetchLogger = finishMillis;
        }
        return record;
    }

    public E push(E record) throws InterruptedException
    {
        long waitTimeout = argval.waitTimeout;
        long logDuration = argval.logDuration;
        long startsMillis = System.currentTimeMillis();
        boolean success = this.tqueue.offer(record, waitTimeout, TimeUnit.MILLISECONDS);
        long finishMillis = System.currentTimeMillis();
        metric.offerMillis += (finishMillis - startsMillis);
        ++metric.offerCounts;
        if (success) {
            ++metric.offerRecord;
            return null;
        }
        else if (finishMillis - metric.offerLogger >= logDuration) {
            logger.info("tqueue.push timeout: waitTimeout={}, logDuration={}" //
                    , waitTimeout, logDuration);
            metric.offerLogger = finishMillis;
        }
        return record;
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ObjectNode configNode = node.putObject("config");
        this.argval.toJsonObject(configNode);
        ObjectNode metricNode = node.putObject("metric");
        this.metric.toJsonObject(metricNode);
        metricNode.set("record_count", new LongNode(this.tqueue.size()));
        return node;
    }
}
