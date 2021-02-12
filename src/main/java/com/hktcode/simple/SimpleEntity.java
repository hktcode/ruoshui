/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.exception.RuoshuiLockedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public abstract class SimpleEntity<R extends SimpleResult>
{
    private final static Logger logger = LoggerFactory.getLogger(SimpleEntity.class);

    private final AtomicReference<SimplePhaser> status;

    protected SimpleEntity()
    {
        this.status = new AtomicReference<>(SimplePhaserInner.of(Long.MAX_VALUE));
    }

    public SimplePhaserInner run(SimpleMetric metric) throws InterruptedException
    {
        SimplePhaser origin;
        while (!((origin = this.status.get()) instanceof SimplePhaserInner)) {
            metric.exeDateTime = System.currentTimeMillis();
            ((SimplePhaserOuter)origin).run();
        }
        SimplePhaserInner result = (SimplePhaserInner) origin;
        if (result.deletets != Long.MAX_VALUE && metric.endDatetime == Long.MAX_VALUE) {
            metric.endDatetime = metric.exeDateTime;
        }
        return result;
    }

    public R run(SimplePhaserOuter cmd, BiFunction<SimplePhaserInner, SimplePhaserOuter, R> keeper) //
            throws InterruptedException
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        if (keeper == null) {
            throw new ArgumentNullException("keeper");
        }
        SimplePhaser curval = this.status.get();
        if (!(curval instanceof SimplePhaserInner)) {
            throw new RuoshuiLockedException();
        }
        SimplePhaserInner origin = (SimplePhaserInner)curval;
        SimplePhaserOuter future = origin.cmd(cmd);
        if (future == cmd && !this.status.compareAndSet(origin, future)) {
            throw new RuoshuiLockedException();
        }
        return future.run(origin, (o, f)->this.apply(o, f, keeper));
    }

    public R end(SimplePhaserOuter cmd, BiFunction<SimplePhaserInner, SimplePhaserOuter, R> keeper) //
            throws InterruptedException
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        // 有价值的代码片段
        // - SimplePhaser origin;
        // - SimplePhaserOuter future;
        // - do {
        // -     origin = this.status.get();
        // -     future = origin.cmd(cmd);
        // - } while (/*  */future == origin  //
        // -         /**/|| (/**/future == cmd  //
        // -         /*     */&& !this.status.compareAndSet(origin, future) //
        // -         /*   */)
        // -     /**/);
        SimplePhaser phaser = this.status.get();
        SimplePhaserInner origin;
        SimplePhaserOuter future;
        do {
            while (!(phaser instanceof SimplePhaserInner)) {
                phaser = this.status.get();
            }
            origin = (SimplePhaserInner)phaser;
            future = origin.cmd(cmd);
        } while (future == cmd && !this.status.compareAndSet(origin, future));
        return future.run(origin, (o, f)->this.apply(o, f, keeper));
    }

    protected R apply(SimplePhaserInner origin, SimplePhaserOuter future, BiFunction<SimplePhaserInner, SimplePhaserOuter, R> keeper)
    {
        try {
            R result = keeper.apply(origin, future);
            origin = SimplePhaserInner.of(result.deletets);
            return result;
        } catch (Exception ex) {
            origin = SimplePhaserInner.of(System.currentTimeMillis());
            logger.error("", ex); // TODO:
            throw ex;
        } finally {
            boolean result = this.status.compareAndSet(future, origin);
            logger.debug("cas(future, origin): origin.deletets={}, return={}", origin.deletets, result);
        }
    }

    public abstract R end(SimplePhaserOuter cmd) throws InterruptedException;
}
