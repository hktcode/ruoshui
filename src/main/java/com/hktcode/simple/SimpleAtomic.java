package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleAtomic
{
    public static SimpleAtomic of()
    {
        SimplePhaserInner phaser = SimplePhaserInner.of(Long.MAX_VALUE);
        AtomicReference<SimplePhaser> atomic = new AtomicReference<>(phaser);
        return new SimpleAtomic(atomic);
    }

    public <R extends  SimpleResult> R call(SimpleMethod<R> method) //
            throws InterruptedException
    {
        if (method == null) {
            throw new ArgumentNullException("method");
        }
        SimplePhaserOuter cmdval = SimplePhaserOuter.of(4);
        SimplePhaser curval = this.atomic.get();
        if (!(curval instanceof SimplePhaserInner)) {
            throw new SimpleLockedException();
        }
        SimplePhaserInner oldval = (SimplePhaserInner)curval;
        long delete = oldval.deletets;
        SimplePhaserOuter newval = oldval.cmd(cmdval);
        if (newval == cmdval && !this.atomic.compareAndSet(oldval, newval)) {
            throw new SimpleLockedException();
        }
        try {
            newval.acquire();
            try {
                R result = method.call(delete);
                if (result.deletets != oldval.deletets) {
                    oldval = SimplePhaserInner.of(result.deletets);
                }
                return result;
            } finally {
                newval.release();
            }
        } finally {
            boolean result = atomic.compareAndSet(newval, oldval);
            if (!result) {
                logger.debug("cas(future, origin): delete={}", delete);
            }
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

    private final AtomicReference<SimplePhaser> atomic;

    private SimpleAtomic(AtomicReference<SimplePhaser> atomic)
    {
        this.atomic = atomic;
    }

    @FunctionalInterface
    public interface SimpleMethod<R extends SimpleResult>
    {
        R call(long delete);
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleAtomic.class);
}
