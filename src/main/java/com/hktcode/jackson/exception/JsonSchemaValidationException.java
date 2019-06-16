/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.jackson.exception;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * {@link com.fasterxml.jackson.databind.JsonNode}不符合指定的JsonSchema时抛出的异常.
 *
 * TODO: 说明此处{@link JsonSchemaValidationException}和{@link JsonSchemaValidationImplException}的设计原理.
 */
public abstract class JsonSchemaValidationException extends RuntimeException
{
    /**
     * 或者验证时的消息数组.
     *
     * @return 验证时出现错误的消息数组.
     */
    public abstract ProcessingMessage[] getProcessingMessages();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "JsonSchemaValidationException: " + this.getMessage();
    }
}
