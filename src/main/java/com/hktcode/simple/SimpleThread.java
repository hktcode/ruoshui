package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleThread<C extends SimpleConfig, M extends SimpleMetric, H extends SimpleEntity<?>> //
        extends Thread implements JacksonObject
{
    private static final Logger logger = LoggerFactory.getLogger(SimpleThread.class);

    protected SimpleThread(C config, M metric, H holder)
    {
        this.config = config;
        this.metric = metric;
        this.holder = holder;
        this.setDaemon(false);
    }

    protected final H holder;

    public final C config;

    public final M metric;

    public abstract SimpleActionRun<C, M, H> createAction();

    private void runWithInterrupted() throws InterruptedException
    {
        SimpleAction<C, M, H> action = this.createAction();
        do {
            SimpleActionRun<C, M, H> a = (SimpleActionRun<C, M, H>)action;
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
    }

    @Override
    public void run()
    {
        this.metric.actionStart = System.currentTimeMillis();
        try {
            this.runWithInterrupted();
        } catch (InterruptedException e) {
            logger.error("should never happen", e);
            Thread.currentThread().interrupt();
        }
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

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.config.pst(node);
    }
}
