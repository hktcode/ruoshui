package com.hktcode.simple;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.TqueueGauges;

import java.util.ArrayList;
import java.util.List;

public class SimpleWkstepGauges extends TqueueGauges implements JacksonObject
{
    public long wkstepStart = Long.MAX_VALUE;

    public long endDatetime = Long.MAX_VALUE;

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
        node.put("wkstep_start", this.wkstepStart);
        node.put("end_datetime", this.endDatetime);
        ArrayNode throwErrorsNode = node.putArray("throw_errors");
        for (Throwable t: this.throwErrors) {
            ObjectNode e = throwErrorsNode.addObject();
            JacksonObject.toJsonObject(t, e);
        }
        node.put("status_infor", this.statusInfor);
        return node;
    }
}
