/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.naive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public final class Naive
{
    private static final Logger logger = LoggerFactory.getLogger(Naive.class);

    public static
        /* */< T
        /* */, F extends NaiveConfig
        /* */, M extends NaiveMutableMetric
        /* */>
    T push(T record, F config, M metric, BlockingQueue<T> queue) //
        throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long startsMillis = System.currentTimeMillis();
        metric.statusInfor = "offer record wait";
        boolean success = queue.offer(record, waitTimeout, TimeUnit.MILLISECONDS);
        metric.statusInfor = "offer record end";
        long finishMillis = System.currentTimeMillis();
        metric.offerMillis += (finishMillis - startsMillis);
        ++metric.offerCounts;
        if (success) {
            ++metric.recordCount;
            return null;
        }
        else {
            long logDuration = config.logDuration;
            long currMillis = System.currentTimeMillis();
            if (currMillis - metric.logDatetime >= logDuration) {
                logger.info("push record to comein fail: timeout={}, logDuration={}", waitTimeout, logDuration);
                metric.logDatetime = currMillis;
            }
            return record;
        }
    }

    public static
    /* */< T
    /* */, F extends NaiveConfig
    /* */, M extends NaiveMutableMetric
    /* */>
    T poll(F config, M metric, BlockingQueue<T> queue) //
        throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long logDuration = config.logDuration;
        long startsMillis = System.currentTimeMillis();
        metric.statusInfor = "fetch record wait";
        T record = queue.poll(waitTimeout, TimeUnit.MILLISECONDS);
        metric.statusInfor = "fetch record end";
        long finishMillis = System.currentTimeMillis();
        metric.fetchMillis += (finishMillis - startsMillis);
        ++metric.fetchCounts;
        if (record == null) {
            long currMillis = System.currentTimeMillis();
            if (currMillis - metric.logDatetime >= logDuration) {
                logger.info("poll record from getout timeout: timeout={}, logDuration={}", waitTimeout, logDuration);
                metric.logDatetime = currMillis;
            }
        }
        return record;
    }
}
