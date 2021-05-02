package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleWorker<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleExesvc>
        implements JacksonObject, Runnable
{
    public final C config;

    public final M metric;

    public final E exesvc;

    protected SimpleWorker(C config, M metric, E exesvc)
    {
        this.config = config;
        this.metric = metric;
        this.exesvc = exesvc;
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.config.pst(node);
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ObjectNode configNode = node.putObject("config");
        this.config.toJsonObject(configNode);
        ObjectNode metricNode = node.putObject("metric");
        this.metric.toJsonObject(metricNode);
        return node;
    }

    public abstract SimpleActionRun<C, M, E> action();

    public void run()
    {
        try {
            SimpleAction action = this.action();
            do {
                @SuppressWarnings("unchecked")
                SimpleActionRun<C, M, E> a = (SimpleActionRun<C, M, E>) action;
                try {
                    action = a.next(this.config, this.metric, this.exesvc);
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    logger.error("triple throws exception: ", ex);
                    action = a.next(this.config, this.metric, this.exesvc, ex);
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
