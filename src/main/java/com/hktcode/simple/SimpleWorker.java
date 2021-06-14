package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleWorker<A extends SimpleWorkerArgval<A, G>, G extends SimpleWorkerGauges>
        implements Runnable
{
    public static <A extends SimpleWorkerArgval<A, G>, G extends SimpleWorkerGauges> //
    SimpleWorker<A, G> of(A argval, G gauges, SimpleAtomic holder)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (gauges == null) {
            throw new ArgumentNullException("gauges");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new SimpleWorker<>(argval, gauges, holder);
    }

    public final A argval;

    public final G gauges;

    private final SimpleAtomic holder;

    protected SimpleWorker(A argval, G gauges, SimpleAtomic holder)
    {
        this.argval = argval;
        this.gauges = gauges;
        this.holder = holder;
    }

    public void run()
    {
        try {
            SimpleWkstep wkstep = this.argval.action();
            do {
                @SuppressWarnings("unchecked")
                SimpleWkstepAction<A, G> action = (SimpleWkstepAction<A, G>) wkstep;
                try {
                    wkstep = action.next(this.argval, this.gauges, this.holder);
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    logger.error("triple throws exception: ", ex);
                    long endMillis = System.currentTimeMillis();
                    gauges.throwErrors.add(ex);
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
            gauges.endDatetime = System.currentTimeMillis();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleWorker.class);
}
