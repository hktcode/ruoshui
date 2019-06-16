/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.jackson.exception;

import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@link com.fasterxml.jackson.databind.JsonNode}不符合指定的JsonSchema时抛出的异常（实现类）.
 *
 * TODO: 说明此处{@link JsonSchemaValidationException}和{@link JsonSchemaValidationImplException}的设计原理.
 */
public final class JsonSchemaValidationImplException extends JsonSchemaValidationException
{
    /**
     * 验证报告.
     */
    public final ProcessingReport report;

    /**
     * 构造函数.
     *
     * @param report 验证报告.
     * @throws ArgumentNullException 当参数{@code report}为{@code null}时抛出.
     */
    public JsonSchemaValidationImplException(ProcessingReport report)
    {
        if (report == null) {
            throw new ArgumentNullException("report");
        }
        this.report = report;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessingMessage[] getProcessingMessages()
    {
        List<ProcessingMessage> result = new ArrayList<>();
        for (ProcessingMessage msg : this.report) {
            result.add(msg);
        }
        return result.toArray(new ProcessingMessage[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage()
    {
        StringBuilder sb = new StringBuilder();
        Iterator<ProcessingMessage> iter = report.iterator();
        if (iter.hasNext()) {
            sb.append(iter.next().getMessage());
        }
        while (iter.hasNext()) {
            sb.append(':');
            sb.append(iter.next().getMessage());
        }
        return sb.toString();
    }
}
