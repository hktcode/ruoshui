/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.future.SimpleFutureOuter;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.method.SimpleMethodDel;
import com.hktcode.bgsimple.method.SimpleMethodDelParamsDefault;
import com.hktcode.bgsimple.status.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;

public abstract class Triple implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(Triple.class);

    protected final SimpleHolder status;

    protected final int number;

    protected Triple(SimpleHolder status, int number)
    {
        this.status = status;
        this.number = number;
    }

    protected abstract TripleActionRun createsAction();

    private void runWithInterrupted() throws InterruptedException
    {
        TripleAction action = this.createsAction();
        try {
            do {
                TripleActionRun act = (TripleActionRun)action;
                action = act.next();
            } while (action instanceof TripleActionRun);
            logger.info("triple completes");
        }
        catch (InterruptedException ex) {
            throw ex;
        }
        catch (Exception ex) {
            logger.error("triple throws exception: ", ex);
            TripleActionErr erract = action.next(ex);
            // TODO: 迁移到TripleActionErr的next()中去: 参考PgThread
            SimpleStatusInner o;
            SimpleStatus f;
            do {
                o = erract.newStatus(erract);
                f = o;
                if (o instanceof SimpleStatusInnerRun) {
                    SimpleMethodDel[] method = new SimpleMethodDel[3];
                    for (int i = 0; i < method.length; ++i) {
                        if (i == number) {
                            method[i] = action.del();
                        }
                        else {
                            method[i] = SimpleMethodDelParamsDefault.of();
                        }
                    }
                    Phaser phaser = new Phaser(3);
                    f = SimpleStatusOuterDel.of(method, phaser);
                }
                else if (!isSameEnd(erract, (SimpleStatusInnerEnd)o)) {
                    SimpleStatusInnerEnd end = (SimpleStatusInnerEnd)o;
                    SimpleMethodAllResultEnd<?>[] method = new SimpleMethodAllResultEnd<?>[3];
                    for (int i = 0; i < method.length; ++i) {
                        if (i == number) {
                            method[i] = action.del();
                        }
                        else {
                            method[i] = (SimpleMethodAllResultEnd<?>)end.result.get(i);
                        }
                    }
                    f = SimpleStatusInnerEnd.of(ImmutableList.copyOf(method));
                }
            } while (o != f && !this.status.cas(o, f));
            if (f instanceof SimpleStatusOuterDel) {
                SimpleStatusOuterDel del = (SimpleStatusOuterDel)f;
                SimpleFutureOuter future = del.newFuture(this.status);
                future.get();
            }
            logger.info("triple terminate.");
        }
    }

    private static boolean isSameEnd(TripleActionErr erract, SimpleStatusInnerEnd status)
    {
        SimpleMethodAllResultEnd statusResult = (SimpleMethodAllResultEnd) status.result.get(1);
        if (!(statusResult instanceof TripleResultErr)) {
            return false;
        }
        TripleResultErr rhs = (TripleResultErr)statusResult;
        return rhs.metric == erract.metric && rhs.config == erract.config;
    }

    @Override
    public void run()
    {
        try {
            this.runWithInterrupted();
        } catch (InterruptedException e) {
            logger.error("should never happen", e);
            Thread.currentThread().interrupt();
        }
    }
}
