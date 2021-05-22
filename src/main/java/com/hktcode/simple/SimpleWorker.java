package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public abstract class SimpleWorker<A extends SimpleWorkerArgval, M extends SimpleWorkerMeters, E extends SimpleExesvc>
        implements JacksonObject, Runnable
{
    public final A argval;

    public final M meters;

    public final E exesvc;

    private final AtomicReference<SimplePhaser> atomic;

    protected SimpleWorker(A argval, M meters, E exesvc, AtomicReference<SimplePhaser> atomic)
    {
        this.argval = argval;
        this.meters = meters;
        this.exesvc = exesvc;
        this.atomic = atomic;
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.argval.pst(node);
    }

    protected SimplePhaserInner call(long endMillis) throws InterruptedException
    {
        SimplePhaser oldval;
        while (!((oldval = this.atomic.get()) instanceof SimplePhaserInner)) {
            ((SimplePhaserOuter)oldval).waiting();
        }
        SimplePhaserInner origin = (SimplePhaserInner) oldval;
        if (endMillis == Long.MAX_VALUE || origin.deletets != Long.MAX_VALUE) {
            return origin;
        }
        SimplePhaserInner future = SimplePhaserInner.of(endMillis);
        return this.atomic.compareAndSet(origin, future) ? future : origin;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ObjectNode configNode = node.putObject("config");
        this.argval.toJsonObject(configNode);
        ObjectNode metricNode = node.putObject("metric");
        this.meters.toJsonObject(metricNode);
        return node;
    }

    public abstract SimpleWkstepAction<M, E> action();

    public void run()
    {
        try {
            SimpleWkstep wkstep = this.action();
            do {
                @SuppressWarnings("unchecked")
                SimpleWkstepAction<M, E> action = (SimpleWkstepAction<M, E>) wkstep;
                try {
                    wkstep = action.next(this.meters, this.exesvc);
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    logger.error("triple throws exception: ", ex);
                    long endMillis = System.currentTimeMillis();
                    meters.throwErrors.add(ex);
                    long deletets;
                    do {
                        deletets = this.call(endMillis).deletets;
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
