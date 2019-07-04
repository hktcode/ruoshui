/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class TripleMetric implements TripleEntity
{
    public final ZonedDateTime startMillis;

    public long recordCount = 0;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    /**
     * 描述当前状态的信息.
     */
    public String statusInfor = "";

    protected TripleMetric(ZonedDateTime startMillis)
    {
        this.startMillis = startMillis;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("start_millis", startMillis.toString());
        node.put("record_count", recordCount);
        node.put("fetch_counts", fetchCounts);
        node.put("fetch_millis", fetchMillis);
        node.put("offer_counts", offerCounts);
        node.put("offer_millis", offerMillis);
        node.put("status_infor", statusInfor);
    }
}
