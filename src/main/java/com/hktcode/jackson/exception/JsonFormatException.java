/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.jackson.exception;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hktcode.jackson.HttpStatusJacksonObjectException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.springframework.http.HttpStatus;

/**
 * 当解析不合法的Json串时抛出的异常.
 *
 * 该类其实是{@link JsonProcessingException}的unchecked包装.
 */
public class JsonFormatException extends HttpStatusJacksonObjectException
{
    /**
     * 构造函数.
     *
     * @param cause 使用Jackson解析时抛出的{@link JsonProcessingException}
     * @throws ArgumentNullException 当参数{@code initCause}为{@code null}时抛出.
     */
    public JsonFormatException(JsonProcessingException cause)
    {
        super(HttpStatus.BAD_REQUEST, cause);
        if (cause == null) {
            throw new ArgumentNullException("cause");
        }
    }

    public JsonFormatException(HttpStatus code, JsonProcessingException cause)
    {
        super(code, cause);
        if (code == null) {
            throw new ArgumentNullException("code");
        }
        if (cause == null) {
            throw new ArgumentNullException("cause");
        }
    }

    public JsonFormatException(HttpStatus code, String message, JsonProcessingException cause)
    {
        super(code, message, cause);
        if (code == null) {
            throw new ArgumentNullException("code");
        }
        if (message == null) {
            throw new ArgumentNullException("message");
        }
        if (cause == null) {
            throw new ArgumentNullException("cause");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized JsonProcessingException getCause()
    {
        return (JsonProcessingException)super.getCause();
    }

    /**
     * 获取发生错误的位置信息.
     *
     * 本函数简单调用{@link JsonProcessingException#getLocation()}.
     *
     * @return 表示Json字符串错误位置的对象.
     * @see JsonProcessingException#getLocation()
     */
    public JsonLocation getLocation()
    {
        return this.getCause().getLocation();
    }

    /**
     * 获取原始的错误消息.
     *
     * 本函数简单调用{@link JsonProcessingException#getOriginalMessage()}.
     *
     * @return 原始的错误消息.
     * @see JsonProcessingException#getOriginalMessage()
     */
    public String getOriginalMessage()
    {
        return this.getCause().getOriginalMessage();
    }

    /**
     * TODO: 目前也不知道这个函数的作用.
     *
     * 本函数简单调用{@link JsonProcessingException#getProcessor()}
     *
     * @return TODO:
     */
    public Object getProcessor()
    {
        return this.getCause().getProcessor();
    }

    /**
     * {@inheritDoc}
     *
     * 本函数简单调用{@link JsonProcessingException#getMessage()}.
     *
     * @see JsonProcessingException#getMessage()
     */
    @Override
    public String getMessage()
    {
        return this.getCause().getMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return getClass().getName() + ": " + getMessage();
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        JsonProcessingException cause = this.getCause();
        node.put("message", cause.getOriginalMessage());
        node.set("errcode", TextNode.valueOf(""));
        node.putArray("advises"); // TODO:
        node.put("exclass", this.getClass().getName());
        ArrayNode errdata = node.putArray("errdata");
        ObjectNode data = errdata.addObject();
        JsonLocation location = cause.getLocation();
        if (location != null) {
            Object sourceRef = location.getSourceRef();
            if (sourceRef != null) {
                data.put("source", sourceRef.toString());
            }
            data.put("line", location.getLineNr() - 1);
        }
        return node;
    }
}
