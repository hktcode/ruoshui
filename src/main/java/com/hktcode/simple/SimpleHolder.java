package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleHolder
{
    public static SimpleHolder of()
    {
        AtomicReference<SimplePhaser> atomic = new AtomicReference<>(SimplePhaserInner.of(Long.MAX_VALUE));
        return new SimpleHolder(atomic);
    }

    public <R extends  SimpleResult> R call(long finish, SimpleMethod<R> method) //
            throws InterruptedException
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
        if (deletets == Long.MAX_VALUE) {
            deletets = finish;
        }
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

    public SimplePhaserInner call(long finish) throws InterruptedException
    {
        SimplePhaser oldval;
        while (!((oldval = this.atomic.get()) instanceof SimplePhaserInner)) {
            ((SimplePhaserOuter)oldval).waiting();
        }
        SimplePhaserInner origin = (SimplePhaserInner) oldval;
        if (finish == Long.MAX_VALUE || origin.deletets != Long.MAX_VALUE) {
            return origin;
        }
        SimplePhaserInner future = SimplePhaserInner.of(finish);
        return this.atomic.compareAndSet(origin, future) ? future : origin;
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
