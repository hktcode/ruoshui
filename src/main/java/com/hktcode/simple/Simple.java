/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simple<E, C extends SimpleEntity<?, ?, E>> implements Runnable
{
    public static <E, C extends SimpleEntity<?, ?, E>> //
    Simple<E, C> of(C entity, SimpleHolder<E> holder)
    {
        if (entity == null) {
            throw new ArgumentNullException("entity");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new Simple<>(entity, holder);
    }

    private static final Logger logger = LoggerFactory.getLogger(Simple.class);

    protected final SimpleHolder<E> holder;

    protected final C entity;

    protected Simple(C entity, SimpleHolder<E> holder)
    {
        this.entity = entity;
        this.holder = holder;
    }

    private void runWithInterrupted() throws InterruptedException
    {
        SimpleAction<E> action = this.entity.createAction(this.holder);
        do {
            SimpleActionRun<E> a = (SimpleActionRun<E>)action;
            try {
                action = a.next();
            } catch (InterruptedException ex) {
                throw ex;
            } catch (Exception ex) {
                logger.error("triple throws exception: ", ex);
                action = a.next(ex, this.entity.metric);
            }
        } while (action instanceof SimpleActionRun);
        logger.info("triple completes");
    }

    @Override
    public void run()
    {
        this.entity.metric.actionStart = System.currentTimeMillis();
        try {
            this.runWithInterrupted();
        } catch (InterruptedException e) {
            logger.error("should never happen", e);
            Thread.currentThread().interrupt();
        }
    }
}
