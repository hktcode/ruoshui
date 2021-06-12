package com.hktcode.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleWorker<A extends SimpleWorkerArgval<A, M>, M extends SimpleWorkerMeters>
        implements Runnable
{
    public final A argval;

    public final M meters;

    private final SimpleAtomic holder;

    protected SimpleWorker(A argval, M meters, SimpleAtomic holder)
    {
        this.argval = argval;
        this.meters = meters;
        this.holder = holder;
    }

    public void run()
    {
        try {
            SimpleWkstep wkstep = this.argval.action();
            do {
                @SuppressWarnings("unchecked")
                SimpleWkstepAction<A, M> action = (SimpleWkstepAction<A, M>) wkstep;
                try {
                    wkstep = action.next(this.argval, this.meters, this.holder);
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    logger.error("triple throws exception: ", ex);
                    long endMillis = System.currentTimeMillis();
                    meters.throwErrors.add(ex);
                    long deletets;
                    do {
                        deletets = this.holder.call(endMillis).deletets;
                    } while (deletets == Long.MAX_VALUE);
                    wkstep = SimpleWkstepTheEnd.of();
                }
            } while (wkstep instanceof SimpleWkstepAction);
            logger.info("triple completes");
        } catch (InterruptedException e) {
            logger.error("should never happen", e);
            Thread.currentThread().interrupt();
        } finally {
            meters.endDatetime = System.currentTimeMillis();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleWorker.class);
}
