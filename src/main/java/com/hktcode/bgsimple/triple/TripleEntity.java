/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JACKSON;
import com.hktcode.jackson.JacksonExceptionHandler;

public interface TripleEntity
{
    void toJsonObject(ObjectNode node);

    default ObjectNode toJsonObject()
    {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        this.toJsonObject(node);
        return JACKSON.immutableCopy(node);
    }
}
