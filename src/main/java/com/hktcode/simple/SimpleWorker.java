package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleWorker<A extends SimpleWorkerArgval<A, M>, M extends SimpleWorkerGauges>
        implements Runnable
{
    public static <A extends SimpleWorkerArgval<A, M>, M extends SimpleWorkerGauges> //
    SimpleWorker<A, M> of(A argval, M meters, SimpleAtomic holder)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new SimpleWorker<>(argval, meters, holder);
    }

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
