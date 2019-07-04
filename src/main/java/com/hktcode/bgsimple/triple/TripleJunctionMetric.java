/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class TripleJunctionMetric extends TripleMetric
{
    public static TripleJunctionMetric of(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new TripleJunctionMetric(startMillis);
    }

    public long exeDuration = 0;

    protected TripleJunctionMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("exe_duration", exeDuration);
    }
}
