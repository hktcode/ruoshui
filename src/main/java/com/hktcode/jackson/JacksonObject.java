package com.hktcode.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;

import java.io.InputStream;
import java.util.*;

public interface JacksonObject
{
    default ObjectNode toJsonObject()
    {
        return this.toJsonObject(JsonNodeFactory.instance);
    }

    default ObjectNode toJsonObject(JsonNodeFactory nc)
    {
        if (nc == null) {
            throw new ArgumentNullException("nc");
        }
        ObjectNode result = nc.objectNode();
        return this.toJsonObject(result);
    }

    ObjectNode toJsonObject(ObjectNode node);

    @SuppressWarnings("rawtypes")
    static JsonNode immutableCopy(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        if (node instanceof ValueNode) {
            return node;
        } else {
            return immutableCopy((ContainerNode) node);
        }
    }

    @SuppressWarnings("rawtypes")
    static ContainerNode immutableCopy(ContainerNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        if (node instanceof ArrayNode) {
            return immutableCopy((ArrayNode) node);
        } else {
            return immutableCopy((ObjectNode) node);
        }
    }

    static ArrayNode immutableCopy(ArrayNode node)
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

    static ObjectNode immutableCopy(ObjectNode node)
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

    static void copyTo(ObjectNode source, ObjectNode target)
    {
        if (source == null) {
            throw new ArgumentNullException("source");
        }
        if (target == null) {
            throw new ArgumentNullException("target");
        }
        Iterator<Map.Entry<String, JsonNode>> iter = source.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> item = iter.next();
            String k = item.getKey();
            JsonNode oldnode = item.getValue();
            if (oldnode instanceof ArrayNode) {
                ArrayNode newnode = target.putArray(k);
                copyTo((ArrayNode) oldnode, newnode);
            } else if (oldnode instanceof ObjectNode) {
                ObjectNode newnode = target.putObject(k);
                copyTo((ObjectNode) oldnode, newnode);
            } else {
                target.set(k, oldnode);
            }
        }
    }

    static void copyTo(ArrayNode source, ArrayNode target)
    {
        if (source == null) {
            throw new ArgumentNullException("source");
        }
        if (target == null) {
            throw new ArgumentNullException("target");
        }
        for (JsonNode oldnode : source) {
            if (oldnode instanceof ArrayNode) {
                ArrayNode newnode = target.addArray();
                copyTo((ArrayNode) oldnode, newnode);
            } else if (oldnode instanceof ObjectNode) {
                ObjectNode newnode = target.addObject();
                copyTo((ObjectNode) oldnode, newnode);
            } else {
                target.add(oldnode);
            }
        }
    }

    static ObjectNode getFromResource(Class<?> clazz, String name)
    {
        YAMLMapper mapper = new YAMLMapper();
        try(InputStream input = clazz.getResourceAsStream(name)) {
            return JacksonObject.immutableCopy((ObjectNode)mapper.readTree(input));
        } catch (Exception e) {
            throw new NeverHappenAssertionError(e);
        }
    }

    static void merge(Map<String, String> map, JsonNode objectNode)
    {
        Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields();
        while(it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            map.put(e.getKey(), e.getValue().asText());
        }
    }
}