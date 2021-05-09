package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleHolderExesvc extends SimpleHolder
{
    public static SimpleHolderExesvc of(AtomicReference<SimplePhaser> atomic)
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        return new SimpleHolderExesvc(atomic);
    }

    public SimplePhaser cas(SimplePhaserOuter actual) throws InterruptedException
    {
        if (actual == null) {
            throw new ArgumentNullException("cmd");
        }
        SimplePhaser curval = this.atomic.get();
        if (!(curval instanceof SimplePhaserInner)) {
            return actual;
        }
        SimplePhaserInner origin = (SimplePhaserInner)curval;
        SimplePhaserOuter future = origin.cmd(actual);
        if (future == actual && !this.atomic.compareAndSet(origin, future)) {
            return actual;
        }
        future.acquire();
        return origin;
    }

    private SimpleHolderExesvc(AtomicReference<SimplePhaser> atomic)
    {
        super(atomic);
    }
}
