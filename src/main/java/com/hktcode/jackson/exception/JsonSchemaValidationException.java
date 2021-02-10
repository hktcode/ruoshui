/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.jackson.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.hktcode.jackson.HttpStatusJacksonObjectException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.springframework.http.HttpStatus;

/**
 * {@link com.fasterxml.jackson.databind.JsonNode}不符合指定的JsonSchema时抛出的异常.
 *
 * TODO: 说明此处{@link JsonSchemaValidationException}和{@link JsonSchemaValidationImplException}的设计原理.
 */
public abstract class JsonSchemaValidationException extends HttpStatusJacksonObjectException
{
    public final static HttpStatus CODE = HttpStatus.UNPROCESSABLE_ENTITY;

    protected JsonSchemaValidationException()
    {
        super(CODE);
    }

    protected JsonSchemaValidationException(String message)
    {
        super(CODE, message);
    }

    protected JsonSchemaValidationException(Throwable cause)
    {
        super(CODE, cause);
    }

    protected JsonSchemaValidationException(String message, Throwable cause)
    {
        super(CODE, message, cause);
    }

    protected JsonSchemaValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(CODE, message, cause, enableSuppression, writableStackTrace);
    }

    protected JsonSchemaValidationException(HttpStatus code)
    {
        super(code);
    }

    protected JsonSchemaValidationException(HttpStatus code, String message)
    {
        super(code, message);
    }

    protected JsonSchemaValidationException(HttpStatus code, Throwable cause)
    {
        super(code, cause);
    }

    protected JsonSchemaValidationException(HttpStatus code, String message, Throwable cause)
    {
        super(code, message, cause);
    }

    protected JsonSchemaValidationException(HttpStatus code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(code, message, cause, enableSuppression, writableStackTrace);
    }

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

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("message", this.getMessage());
        node.set("errcode", TextNode.valueOf(""));
        node.putArray("advises"); // TODO:
        node.put("exclass", this.getClass().getName());
        ArrayNode errdata = node.putArray("errdata");
        ProcessingMessage[] messages = this.getProcessingMessages();
        for (ProcessingMessage msg : messages) {
            addObject(errdata, msg);
        }
        return node;
    }

    public static void addObject(ArrayNode entity, ProcessingMessage msg)
    {
        ObjectNode n = entity.addObject();
        n.put("level", msg.getLogLevel().name());
        n.put("message", msg.getMessage());
        JsonNode pointer = TextNode.valueOf("");
        JsonNode instance;
        JsonNode msgJson;
        boolean hasPointer = (msgJson = msg.asJson()) != null
                && msgJson.isObject()
                && (instance = msgJson.get("instance")) != null
                && instance.isObject()
                && (pointer = instance.get("pointer")) != null
                && pointer.isTextual();
        if (hasPointer) {
            n.put("pointer", pointer.asText());
        }
    }
}
