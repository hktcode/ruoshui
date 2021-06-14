package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperQueues;
import com.hktcode.simple.SimpleWorkerArgval;

public class UpjctWorkerArgval implements SimpleWorkerArgval<UpjctWorkerArgval, UpjctWorkerGauges>
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode argvalNode = schema.putObject("properties");
        ObjectNode actionInfosNode = argvalNode.putObject("action_infos");
        actionInfosNode.put("type", "array");
        actionInfosNode.set("items", UpjctWkstepArgval.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpjctWorkerArgval ofJsonObject(JsonNode json, UpperQueues upperQueues) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (upperQueues == null) {
            throw new ArgumentNullException("upperQueues");
        }
        JsonNode actionInfoNode = json.path("action_info");
        ArrayNode arrayNode;
        UpjctWkstepArgval action;
        if (actionInfoNode instanceof MissingNode) {
            action = UpjctWkstepArgval.ofJsonObject(MissingNode.getInstance());
        }
        else if ((arrayNode = (ArrayNode)actionInfoNode).size() == 0) {
            action = UpjctWkstepArgval.ofJsonObject(MissingNode.getInstance());
        }
        else {
            action = UpjctWkstepArgval.ofJsonObject(arrayNode.get(0));
        }
        return new UpjctWorkerArgval(ImmutableList.of(action), upperQueues);
    }

    public final ImmutableList<UpjctWkstepArgval> actionInfos;

    private final UpperQueues upperQueues;

    private UpjctWorkerArgval(ImmutableList<UpjctWkstepArgval> actionInfos, UpperQueues upperQueues)
    {
        this.actionInfos = actionInfos;
        this.upperQueues = upperQueues;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ArrayNode actionInfosNode = node.putArray("action_infos");
        for (UpjctWkstepArgval c: this.actionInfos) {
            ObjectNode n = actionInfosNode.addObject();
            c.toJsonObject(n);
        }
        return node;
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.actionInfos.get(0).pst(node);
    }

    @Override
    public UpjctWkstepAction action()
    {
        return this.actionInfos.get(0).action(this.upperQueues);
    }
}
