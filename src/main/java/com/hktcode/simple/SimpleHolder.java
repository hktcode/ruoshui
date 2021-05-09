package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public abstract class SimpleHolder
{
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

    private static final Logger logger = LoggerFactory.getLogger(SimpleHolder.class);
}
