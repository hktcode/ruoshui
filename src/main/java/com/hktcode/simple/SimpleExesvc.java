/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public abstract class SimpleExesvc extends ThreadPoolExecutor
{
    private final static Logger logger = LoggerFactory.getLogger(SimpleExesvc.class);

    protected final SimpleHolder holder;

    protected SimpleExesvc()
    {
        super(3, 3, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        this.holder = SimpleHolder.of();
    }
}
