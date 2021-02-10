package com.hktcode.simple;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.TqueueMetric;

import java.util.ArrayList;
import java.util.List;

public class SimpleMetric extends TqueueMetric implements JacksonObject
{
    public long actionStart = Long.MAX_VALUE;

    public long endDatetime = Long.MAX_VALUE;

    public long exeDateTime = 0;

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
        node.put("action_start", this.actionStart);
        node.put("end_datetime", this.endDatetime);
        ArrayNode throwErrorsNode = node.putArray("throw_erros");
        for (Throwable t: this.throwErrors) {
            throwErrorsNode.addPOJO(t); // TODO:
            //  - cause: []
            //    message: "Failed to update metadata after 60000 ms."
            //    stack_trace: []
            //    localized_message: "Failed to update metadata after 60000 ms."
            //    suppressed: []
        }
        node.put("status_infor", this.statusInfor);
        return node;
    }
}
