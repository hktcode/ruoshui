package com.hktcode.simple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public abstract class SimpleEntity<C extends SimpleConfig, M extends SimpleMetric, E> //
        implements JacksonObject
{
    protected SimpleEntity(C config, M metric)
    {
        this.config = config;
        this.metric = metric;
    }

    public final C config;

    public final M metric;

    public abstract SimpleActionRun<E> createAction(SimpleHolder<E> holder);

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
