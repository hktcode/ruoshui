package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class JsonResult<C extends JacksonObject, M extends JacksonObject>
    implements JacksonObject
{
    public final C config;

    public final M metric;

    protected JsonResult(C config, M metric)
    {
        this.config = config;
        this.metric = metric;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ObjectNode configNode = node.putObject("config");
        ObjectNode metricNode = node.putObject("metric");
        this.config.toJsonObject(configNode);
        this.metric.toJsonObject(metricNode);
        return node;
    }
}
