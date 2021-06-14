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
    public static <E> Tqueue<E> of(TqueueArgval argval, TqueueGauges gauges) //
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (gauges == null) {
            throw new ArgumentNullException("gauges");
        }
        LinkedBlockingQueue<E> tqueue = new LinkedBlockingQueue<>(argval.maxCapacity);
        return new Tqueue<>(tqueue, argval, gauges);
    }

    private static final Logger logger = LoggerFactory.getLogger(Tqueue.class);

    private BlockingQueue<E> tqueue;
    public final TqueueArgval argval;
    public final TqueueGauges gauges;

    private Tqueue(BlockingQueue<E> tqueue, TqueueArgval argval, TqueueGauges gauges)
    {
        this.tqueue = tqueue;
        this.argval = argval;
        this.gauges = gauges;
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
        gauges.fetchMillis += (finishMillis - startsMillis);
        ++gauges.fetchCounts;
        if (record != null) {
            ++gauges.fetchRecord;
        }
        else if (finishMillis - gauges.fetchLogger >= logDuration) {
            logger.info("tqueue.poll timeout: waitTimeout={}, logDuration={}" //
                    , waitTimeout, logDuration);
            gauges.fetchLogger = finishMillis;
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
        gauges.offerMillis += (finishMillis - startsMillis);
        ++gauges.offerCounts;
        if (success) {
            ++gauges.offerRecord;
            return null;
        }
        else if (finishMillis - gauges.offerLogger >= logDuration) {
            logger.info("tqueue.push timeout: waitTimeout={}, logDuration={}" //
                    , waitTimeout, logDuration);
            gauges.offerLogger = finishMillis;
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
        this.gauges.toJsonObject(metricNode);
        metricNode.set("record_count", new LongNode(this.tqueue.size()));
        return node;
    }
}
