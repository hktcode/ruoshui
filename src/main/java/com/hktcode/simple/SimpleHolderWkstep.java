package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleHolderWkstep extends SimpleHolder
{
    public static SimpleHolderWkstep of(AtomicReference<SimplePhaser> atomic)
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        return new SimpleHolderWkstep(atomic);
    }

    public SimplePhaserInner cas(SimplePhaserOuter actual) throws InterruptedException
    {
        if (actual == null) {
            throw new ArgumentNullException("actual");
        }
        // 有价值的代码片段
        // - SimplePhaser origin;
        // - SimplePhaserOuter future;
        // - do {
        // -     origin = this.atomic.get();
        // -     future = origin.cmd(cmd);
        // - } while (/*  */future == origin  //
        // -         /**/|| (/**/future == cmd  //
        // -         /*     */&& !this.atomic.compareAndSet(origin, future) //
        // -         /*   */)
        // -     /**/);
        SimplePhaser phaser = this.atomic.get();
        SimplePhaserInner origin;
        SimplePhaserOuter future;
        do {
            while (!(phaser instanceof SimplePhaserInner)) {
                phaser = this.atomic.get();
            }
            origin = (SimplePhaserInner)phaser;
            future = origin.cmd(actual);
        } while (future == actual && !this.atomic.compareAndSet(origin, future));
        future.acquire();
        return origin;
    }

    public SimplePhaserInner run(SimpleWorkerMeters metric) throws InterruptedException
    {
        SimplePhaser origin;
        while (!((origin = this.atomic.get()) instanceof SimplePhaserInner)) {
            metric.exeDatetime = System.currentTimeMillis();
            ((SimplePhaserOuter)origin).waiting();
        }
        SimplePhaserInner result = (SimplePhaserInner) origin;
        if (result.deletets != Long.MAX_VALUE && metric.endDatetime == Long.MAX_VALUE) {
            metric.endDatetime = metric.exeDatetime;
        }
        return result;
    }

    private SimpleHolderWkstep(AtomicReference<SimplePhaser> atomic)
    {
        super(atomic);
    }
}
