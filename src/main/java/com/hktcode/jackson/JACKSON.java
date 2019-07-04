/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.*;

public class JACKSON
{
    private JACKSON()
    {

    }

    public static JsonNode immutableCopy(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        if (node instanceof ValueNode) {
            return node;
        }
        else {
            return immutableCopy((ContainerNode)node);
        }
    }

    public static ContainerNode immutableCopy(ContainerNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        if (node instanceof ArrayNode) {
            return immutableCopy((ArrayNode)node);
        }
        else {
            return immutableCopy((ObjectNode)node);
        }
    }

    public static ArrayNode immutableCopy(ArrayNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        List<JsonNode> result = new ArrayList<>(node.size());
        Iterator<JsonNode> iter = node.elements();
        while (iter.hasNext()) {
            result.add(immutableCopy(iter.next()));
        }
        ImmutableList<JsonNode> elements = ImmutableList.copyOf(result);
        return new ArrayNode(JsonNodeFactory.instance, elements);
    }

    public static ObjectNode immutableCopy(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        Map<String, JsonNode> result = new HashMap<>(node.size());
        Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> item = iter.next();
            String k = item.getKey();
            JsonNode v = item.getValue();
            result.put(k, immutableCopy(v));
        }
        ImmutableMap<String, JsonNode> elements = ImmutableMap.copyOf(result);
        return new ObjectNode(JsonNodeFactory.instance, elements);
    }
}
