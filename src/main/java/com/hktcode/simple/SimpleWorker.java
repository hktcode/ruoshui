package com.hktcode.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleWorker implements Runnable
{
    private final SimpleAtomic atomic;

    public long starts = Long.MAX_VALUE;

    public long finish = Long.MAX_VALUE;

    // - public final List<SimpleWkstepGauges> wkstep = new ArrayList<>();

    public final List<Throwable> errors = new ArrayList<>();

    protected SimpleWorker(SimpleAtomic atomic)
    {
        this.atomic = atomic;
    }

    public void run()
    {
        try {
            try {
                this.starts = System.currentTimeMillis();
                this.run(this.atomic);
                logger.info("triple completes");
            } catch (Throwable ex) {
                long endMillis = System.currentTimeMillis();
                this.errors.add(ex);
                long deletets;
                do {
                    deletets = this.atomic.call(endMillis).deletets;
                } while (deletets == Long.MAX_VALUE);
                throw ex;
            }
        } catch (InterruptedException ex) {
            logger.error("should never happen", ex);
            Thread.currentThread().interrupt();
        } catch (Throwable ex) {
            logger.error("triple throws exception: ", ex);
        }
        finally {
            this.finish = System.currentTimeMillis();
        }
    }

    protected abstract void run(SimpleAtomic atomic) throws Throwable;

    private static final Logger logger = LoggerFactory.getLogger(SimpleWorker.class);
}
