/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.SimpleHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        while (!(action instanceof TripleActionEnd)) {
            try {
                action = action.next();
            } catch (InterruptedException ex) {
                throw ex;
            } catch (Exception ex) {
                logger.error("triple throws exception: ", ex);
                action = action.next(ex);
            }
        }
        logger.info("triple completes.");
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
