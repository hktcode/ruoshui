package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleWorker<A extends SimpleArgval, M extends SimpleMeters, E extends SimpleExesvc>
        implements JacksonObject, Runnable
{
    public final A argval;

    public final M meters;

    public final E exesvc;

    protected SimpleWorker(A argval, M meters, E exesvc)
    {
        this.argval = argval;
        this.meters = meters;
        this.exesvc = exesvc;
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.argval.pst(node);
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

    public abstract SimpleActionRun<M, E> action();

    public void run()
    {
        try {
            SimpleAction action = this.action();
            do {
                @SuppressWarnings("unchecked")
                SimpleActionRun<M, E> a = (SimpleActionRun<M, E>) action;
                try {
                    action = a.next(this.meters, this.exesvc);
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    logger.error("triple throws exception: ", ex);
                    meters.throwErrors.add(ex);
                    meters.endDatetime = System.currentTimeMillis();
                    SimplePhaserOuter del = SimplePhaserOuter.of(3);
                    while (exesvc.run(meters).deletets == Long.MAX_VALUE) {
                        SimpleResult result = exesvc.end(del);
                        logger.info("end: result={}", result);
                    }
                    action = SimpleFinish.of();
                }
            } while (action instanceof SimpleActionRun);
            logger.info("triple completes");
        } catch (InterruptedException e) {
            logger.error("should never happen", e);
            Thread.currentThread().interrupt();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleWorker.class);
}
