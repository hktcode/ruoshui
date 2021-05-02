package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleThread implements JacksonObject, Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(SimpleThread.class);

    public static SimpleThread of(SimpleActionRun<?, ?, ?> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SimpleThread(action);
    }

    protected SimpleThread(SimpleActionRun<?, ?, ?> action)
    {
        this.action = action;
    }

    public SimpleAction<?, ?, ?> action;

    @Override
    public void run()
    {
        this.action.metric.actionStart = System.currentTimeMillis();
        try {
            do {
                SimpleActionRun<?, ?, ?> a = (SimpleActionRun<?, ?, ?>)action;
                try {
                    action = a.next();
                } catch (InterruptedException ex) {
                    throw ex;
                } catch (Throwable ex) {
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
        this.action.config.pst(node);
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ObjectNode configNode = node.putObject("config");
        this.action.config.toJsonObject(configNode);
        ObjectNode metricNode = node.putObject("metric");
        this.action.metric.toJsonObject(metricNode);
        return node;
    }
}
