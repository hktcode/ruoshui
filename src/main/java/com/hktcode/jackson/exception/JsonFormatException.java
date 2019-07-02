/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.jackson.exception;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 当解析不合法的Json串时抛出的异常.
 *
 * 该类其实是{@link JsonProcessingException}的unchecked包装.
 */
public class JsonFormatException extends RuntimeException
{
    /**
     * 构造函数.
     *
     * @param initCause 使用Jackson解析时抛出的{@link JsonProcessingException}
     * @throws ArgumentNullException 当参数{@code initCause}为{@code null}时抛出.
     */
    public JsonFormatException(JsonProcessingException initCause)
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
    public JsonProcessingException getCause()
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
}
