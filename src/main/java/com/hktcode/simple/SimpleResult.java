package com.hktcode.simple;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleResult implements JacksonObject
{
    public final String fullname;

    public final long createts;

    public final long deletets;

    // - public final ImmutableList<ObjectNode> laborers;

    protected SimpleResult(String fullname, long createts, long deletets)
    {
        this.fullname = fullname;
        this.createts = createts;
        this.deletets = deletets;
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("fullname", this.fullname);
        node.put("createts", this.createts);
        node.put("deletets", this.deletets);
        return node;
    }
}
