/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.jackson.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.RequestPayload;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 当解析不合法的Json串时抛出的异常.
 *
 * 该类其实是{@link JsonParseException}的unchecked包装.
 *
 * TODO: 弄清楚Jackson中{@link JsonParseException}和{@link JsonProcessingException}的区别.
 */
public class JsonFormatParseException extends JsonFormatException
{
    /**
     * 构造函数.
     *
     * @param initCause 使用Jackson解析时抛出的{@link JsonParseException}
     * @throws 当参数{@code initCause}为{@code null}时抛出.
     */
    public JsonFormatParseException(JsonParseException initCause)
    {
        super(initCause);
        if (initCause == null) {
            throw new ArgumentNullException("initCause");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonParseException getCause()
    {
        return (JsonParseException) super.getCause();
    }

    /**
     * TODO: 此函数的作用尚需研究.
     *
     * 本函数简单调用{@link JsonParseException#getRequestPayload()}.
     *
     * @return TODO:
     */
    public RequestPayload getRequestPayload()
    {
        return this.getCause().getRequestPayload();
    }

    /**
     * TODO: 此函数的作用尚需研究.
     *
     * 本函数简单调用{@link JsonParseException#getRequestPayloadAsString()} ()}.
     *
     * @return TODO:
     */
    public String getRequestPayloadAsString()
    {
        return this.getCause().getRequestPayloadAsString();
    }
}
