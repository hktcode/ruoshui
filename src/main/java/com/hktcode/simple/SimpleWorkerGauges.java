package com.hktcode.simple;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.ArrayList;
import java.util.List;

public class SimpleWorkerGauges implements JacksonObject
{
    public long starts = Long.MAX_VALUE;

    public long finish = Long.MAX_VALUE;

    public final List<SimpleWkstepGauges> wkstep = new ArrayList<>();

    public final List<Throwable> errors = new ArrayList<>();

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("worker_start", this.starts);
        node.put("end_datetime", this.finish);
        ArrayNode actionInfosNode = node.putArray("action_infos");
        for (SimpleWkstepGauges m: this.wkstep) {
            ObjectNode n = actionInfosNode.addObject();
            m.toJsonObject(n);
        }
        ArrayNode throwErrorsNode = node.putArray("throw_errors");
        for (Throwable t: this.errors) {
            ObjectNode e = throwErrorsNode.addObject();
            JacksonObject.toJsonObject(t, e);
        }
        return node;
    }
}
