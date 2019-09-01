/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.bgsimple.method.SimpleMethodAllResult;

public abstract class MainlineResult<M extends MainlineMetric>
    implements SimpleMethodAllResult<MainlineAction>
{
    public final MainlineConfig config;

    public final M metric;

    protected MainlineResult(MainlineConfig config, M metric)
    {
        this.config = config;
        this.metric = metric;
    }

    public ObjectNode toJsonObject()
    {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        this.toJsonObject(node);
        return node;
    }

    public abstract void toJsonObject(ObjectNode node);
}
