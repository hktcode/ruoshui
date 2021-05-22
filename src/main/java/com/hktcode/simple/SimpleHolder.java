package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public abstract class SimpleHolder
{
    public <R extends  SimpleResult> R call(SimpleMethod<R> method) throws InterruptedException
    {
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        SimplePhaserOuter newval = SimplePhaserOuter.of(4);
        SimplePhaser curval = this.atomic.get();
        if (!(curval instanceof SimplePhaserInner)) {
            throw new RuntimeException(); //  未来计划：
        }
        SimplePhaserInner origin = (SimplePhaserInner)curval;
        SimplePhaserOuter future = origin.cmd(newval);
        if (future == newval && !this.atomic.compareAndSet(origin, future)) {
            throw new RuntimeException(); //  未来计划：
        }
        long deletets = origin.deletets;
        future.acquire();
        try {
            return method.call(deletets);
        }
        finally {
            boolean result = atomic.compareAndSet(future, origin);
            if (!result) {
                logger.debug("cas(future, origin): origin.deletets={}", deletets);
            }
            future.release();
        }
    }

    public SimplePhaser set(SimplePhaserInner actual) throws InterruptedException
    {
        if (actual == null) {
            throw new ArgumentNullException("actual");
        }
        SimplePhaser curval = this.atomic.get();
        if (!(curval instanceof SimplePhaserOuter)) {
            logger.error("set inner phaser fail: actual={}, curval={}", actual.deletets, curval);
            return actual;
        }
        SimplePhaserOuter future = (SimplePhaserOuter) curval;
        boolean result = this.atomic.compareAndSet(future, actual);
        if (!result) {
            logger.debug("cas(future, origin): origin.deletets={}", actual.deletets);
        }
        future.release();
        return future;
    }

    protected final AtomicReference<SimplePhaser> atomic;

    protected SimpleHolder(AtomicReference<SimplePhaser> atomic)
    {
        this.atomic = atomic;
    }

    @FunctionalInterface
    public interface SimpleMethod<R extends SimpleResult>
    {
        R call(long deletets);
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleHolder.class);
}
