package com.hktcode.jackson;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public abstract class JacksonObjectException extends RuntimeException
    implements JacksonObject
{
    protected JacksonObjectException()
    {
        super();
    }

    protected JacksonObjectException(String message)
    {
        super(message);
    }

    protected JacksonObjectException(Throwable cause)
    {
        super(cause);
    }

    protected JacksonObjectException(String message, Throwable cause)
    {
        super(message, cause);
    }

    protected JacksonObjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put(ERRCODE, "");
        node.put(MESSAGE, "");
        node.put(EXCLASS, "");
        node.putArray(ERRDATA);
        node.putArray(REASONS);
        node.putArray(ADVISES);
        return node;
    }

    public static final String ERRCODE = "errcode";
    public static final String MESSAGE = "message";
    public static final String EXCLASS = "exclass";
    public static final String ERRDATA = "errdata";
    public static final String REASONS = "reasons";
    public static final String ADVISES = "advises";
}
