package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleThread<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleEntity<?>> //
        extends Thread implements JacksonObject
{
    private static final Logger logger = LoggerFactory.getLogger(SimpleThread.class);

    protected SimpleThread(C config, M metric, E entity)
    {
        this.config = config;
        this.metric = metric;
        this.entity = entity;
        this.setDaemon(false);
    }

    protected final E entity;

    public final C config;

    public final M metric;

    public abstract SimpleActionRun<C, M, E> createAction();

    @Override
    public void run()
    {
        this.metric.actionStart = System.currentTimeMillis();
        try {
            SimpleAction<C, M, E> action = this.createAction();
            do {
                SimpleActionRun<C, M, E> a = (SimpleActionRun<C, M, E>)action;
                try {
                    action = a.next();
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Exception ex) {
                    logger.error("triple throws exception: ", ex);
                    action = a.next(ex);
                }
            } while (action instanceof SimpleActionRun);
            logger.info("triple completes");
        } catch (InterruptedException e) {
            logger.error("should never happen", e);
            Thread.currentThread().interrupt();
        }
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
}
