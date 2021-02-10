/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.jackson.exception;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * {@link com.fasterxml.jackson.databind.JsonNode}不符合指定的JsonSchema时抛出的异常.
 *
 * 该类其实是{@link ProcessingException}的unchecked包装.
 *
 */
public class JsonSchemaValidationProcessingException extends JsonSchemaValidationException
{
    /**
     * 构造函数.
     *
     * @param initCause 使用json-schema-validator验证时抛出的异常.
     * @throws ArgumentNullException 当参数{@code initCause}为{@code null}时抛出.
     */
    public JsonSchemaValidationProcessingException(ProcessingException initCause)
    {
        if (initCause == null) {
            throw new ArgumentNullException("initCause");
        }
        super.initCause(initCause);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ProcessingException getCause()
    {
        return (ProcessingException)super.getCause();
    }

    /**
     * {@inheritDoc}
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
    public ProcessingMessage[] getProcessingMessages()
    {
        return new ProcessingMessage[] { getCause().getProcessingMessage() };
    }

    /**
     * TODO: 此函数的作用尚需研究.
     *
     * 本函数简单调用{@link ProcessingException#getShortMessage()}.
     *
     * @return TODO:
     */
    public String getShortMessage()
    {
        return this.getCause().getShortMessage();
    }

    /**
     * 返回导致该异常的{@link ProcessingMessage}.
     *
     * 本函数简单调用{@link ProcessingException#getProcessingMessage()}.
     *
     * @return 导致该异常的{@link ProcessingMessage}对象.
     */
    public ProcessingMessage getProcessingMessage()
    {
        return this.getCause().getProcessingMessage();
    }
}
