package com.hktcode.simple;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.ArrayList;
import java.util.List;

public class SimpleWorkerGauges implements JacksonObject
{
    public long workerStart = Long.MAX_VALUE;

    public long endDatetime = Long.MAX_VALUE;

    public long exeDatetime = 0;

    public final List<SimpleWkstepGauges> actionInfos = new ArrayList<>();

    public final List<Throwable> throwErrors = new ArrayList<>();

    /**
     * 描述当前状态的信息.
     */
    public String statusInfor = "";

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("worker_start", this.workerStart);
        node.put("end_datetime", this.endDatetime);
        ArrayNode actionInfosNode = node.putArray("action_infos");
        for (SimpleWkstepGauges m: this.actionInfos) {
            ObjectNode n = actionInfosNode.addObject();
            m.toJsonObject(n);
        }
        ArrayNode throwErrorsNode = node.putArray("throw_errors");
        for (Throwable t: this.throwErrors) {
            ObjectNode e = throwErrorsNode.addObject();
            JacksonObject.toJsonObject(t, e);
        }
        node.put("status_infor", this.statusInfor);
        return node;
    }
}
