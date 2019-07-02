/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.jackson.exception;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * Json字符串不完整时抛出的异常.
 *
 * 该类其实是{@link JsonEOFException}的unchecked包装.
 */
public class JsonFormatEOFException extends JsonFormatParseException
{
    /**
     * 构造函数.
     *
     * @param initCause 使用Jackson解析时抛出的{@link JsonEOFException}
     * @throws ArgumentNullException 当参数{@code initCause}为{@code null}时抛出.
     */
    public JsonFormatEOFException(JsonEOFException initCause)
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
    public JsonEOFException getCause()
    {
        return (JsonEOFException) super.getCause();
    }

    /**
     * 获取正在解析的Token信息.
     *
     * TODO: 该函数的具体含义细节还需要研究.
     *
     * 本函数简单调用{@link JsonEOFException#getTokenBeingDecoded()}.
     *
     * @return 表示正在解析的JsonToken.
     * @see JsonEOFException#getTokenBeingDecoded()
     */
    public JsonToken getTokenBeingDecoded()
    {
        return this.getCause().getTokenBeingDecoded();
    }
}
