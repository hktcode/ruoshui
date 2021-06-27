package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SimpleWorker<A extends SimpleWorkerArgval<A>>
        implements Runnable
{
    public static <A extends SimpleWorkerArgval<A>> //
    SimpleWorker<A> of(A argval, SimpleAtomic atomic)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        return new SimpleWorker<>(argval, atomic);
    }

    public final A argval;

    private final SimpleAtomic atomic;

    public long starts = Long.MAX_VALUE;

    public long finish = Long.MAX_VALUE;

    // - public final List<SimpleWkstepGauges> wkstep = new ArrayList<>();

    public final List<Throwable> errors = new ArrayList<>();

    protected SimpleWorker(A argval, SimpleAtomic atomic)
    {
        this.argval = argval;
        this.atomic = atomic;
    }

    public void run()
    {
        try {
            this.starts = System.currentTimeMillis();
            SimpleWkstep wkstep = this.argval.action();
            do {
                @SuppressWarnings("unchecked")
                SimpleWkstepAction<A> action = (SimpleWkstepAction<A>) wkstep;
                try {
                    wkstep = action.next(this.atomic);
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    logger.error("triple throws exception: ", ex);
                    long endMillis = System.currentTimeMillis();
                    this.errors.add(ex);
                    long deletets;
                    do {
                        deletets = this.atomic.call(endMillis).deletets;
                    } while (deletets == Long.MAX_VALUE);
                    wkstep = SimpleWkstepTheEnd.of();
                }
            } while (wkstep instanceof SimpleWkstepAction);
            logger.info("triple completes");
        } catch (InterruptedException e) {
            logger.error("should never happen", e);
            Thread.currentThread().interrupt();
        } finally {
            this.finish = System.currentTimeMillis();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleWorker.class);
}
