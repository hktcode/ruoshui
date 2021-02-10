package com.hktcode.queue;

import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class TqueueMetric implements JacksonObject
{
    public static TqueueMetric of()
    {
        return new TqueueMetric();
    }

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long fetchRecord = 0;

    public long fetchLogger = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long offerRecord = 0;

    public long offerLogger = 0;

    public long logDatetime = 0;

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.set("fetch_counts", new LongNode(this.fetchCounts));
        node.set("fetch_millis", new LongNode(this.fetchMillis));
        node.set("fetch_record", new LongNode(this.fetchRecord));
        node.set("offer_counts", new LongNode(this.offerCounts));
        node.set("offer_millis", new LongNode(this.offerMillis));
        node.set("offer_record", new LongNode(this.offerRecord));
        node.set("log_datetime", new LongNode(this.logDatetime));
        return node;
    }
}
