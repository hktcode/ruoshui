package com.hktcode.simple;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
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

    @Override
    public void run()
    {
        this.starts = System.currentTimeMillis();
        try {
            try {
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

    public static class Metric implements JacksonObject
    {
        public final long starts;
        public final long finish;
        public final ImmutableList<Throwable> errors;

        protected Metric(SimpleWorker worker)
        {
            this.starts = worker.starts;
            this.finish = worker.finish;
            this.errors = ImmutableList.copyOf(worker.errors);
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node.put("starts", this.starts);
            node.put("finish", this.finish);
            ArrayNode errorsNode = node.putArray("errors");
            for (Throwable t : this.errors) {
                errorsNode.addPOJO(t);
            }
            return node;
        }
    }
}
