/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.jackson;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Jackson序列化库的异常Handler.
 *
 * TODO: 编写文档说明各个异常转换成的Json对象的各个字段，或者编写JsonSchema说明.
 */
@RestControllerAdvice
public class JacksonExceptionHandler
{
    private static final Logger logger = LoggerFactory.getLogger(JacksonExceptionHandler.class);

    private static final String MESSAGE_FIELD_NAME = "message";

    public static JacksonExceptionHandler of(ObjectMapper mapper)
    {
        if (mapper == null) {
            throw new ArgumentNullException("mapper");
        }
        return new JacksonExceptionHandler(mapper);
    }

    private final ObjectMapper mapper;

    private JacksonExceptionHandler(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }
    /**
     * 将{@link ProcessingMessage}对象转换为Json对象添加到指定的Json数组中.
     *
     * @param uri 请求的uri.
     * @param entity 要添加对象的Json数组.
     * @param msg 转换称为Json对象的{@link ProcessingMessage}对象.
     */
    private static void addObject(String uri, ArrayNode entity, ProcessingMessage msg)
    {
        ObjectNode n = entity.addObject();
        n.put("level", msg.getLogLevel().name());
        n.put(MESSAGE_FIELD_NAME, msg.getMessage());
        JsonNode pointer;
        JsonNode instance;
        JsonNode msgJson;
        if ((msgJson = msg.asJson()) == null) {
            logger.error("msgJson is null: uri={}\n{}", uri, msg);
        }
        else if (!msgJson.isObject()) {
            logger.error("msgJson is not an ObjectNode: uri={}, msgJson={}\n{}", uri, msgJson, msg);
        }
        else if ((instance = msgJson.get("instance")) == null) {
            logger.error("instance is null: uri={}, msgJson={}\n{}", uri, msgJson, msg);
        }
        else if (!instance.isObject()) {
            logger.error("instance is not an ObjectNode: uri={}, instance={}, msgJson={}\n{}", uri, instance, msgJson, msg);
        }
        else if ((pointer = instance.get("pointer")) == null) {
            logger.error("pointer is not exists in instance: uri={}, msgJson={}\n{}", uri, msgJson, msg);
        }
        else if (!pointer.isTextual()) {
            logger.error("pointer is not a textual: uri={}, pointer={}, msgJson={}\n{}", uri, pointer, msgJson, msg);
        }
        else {
            n.put("pointer", pointer.asText());
        }
    }

    /**
     * 向{@link ObjectNode}中添加Json位置信息.
     *
     * @param entity 要添加的{@link ObjectNode}对象.
     * @param location Json位置信息，如果是{@code null}则不添加.
     */
    private static void putLocation(ObjectNode entity, JsonLocation location)
    {
        if (location != null) {
            Object sourceRef = location.getSourceRef();
            if (sourceRef != null) {
                entity.put("source", sourceRef.toString());
            }
            entity.put("line", location.getLineNr() - 1);
        }
    }

    /**
     * 处理{@link JsonProcessingException}异常.
     *
     * @param ex 要处理的{@link JsonProcessingException}异常.
     * @param req 访问的请求.
     * @return {@link JsonProcessingException}转换成的响应对象.
     */
    @ExceptionHandler
    public ResponseEntity<ArrayNode> jsonProcessingExceptionHandler(JsonProcessingException ex, HttpServletRequest req)
    {
        if (ex == null) {
            throw new ArgumentNullException("ex");
        }
        if (req == null) {
            throw new ArgumentNullException("req");
        }
        logger.info("parse body to json failure: uri={}", req.getRequestURI(), ex);

        ArrayNode result = this.mapper.createArrayNode();
        ObjectNode entity = result.addObject();
        entity.put(MESSAGE_FIELD_NAME, ex.getOriginalMessage());
        JsonLocation location = ex.getLocation();
        putLocation(entity, location);
        if (ex instanceof JsonEOFException) {
            JsonToken token = ((JsonEOFException) ex).getTokenBeingDecoded();
            if (token != null) {
                entity.put("token_being_decoded", token.name());
            }
        }
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理{@link ProcessingException}异常.
     *
     * @param ex 要处理的{@link ProcessingException}异常.
     * @param req 访问的请求.
     * @return {@link ProcessingException}转换成的响应对象.
     */
    @ExceptionHandler
    public ResponseEntity<ArrayNode> processingExceptionHandler(ProcessingException ex, HttpServletRequest req)
    {
        if (ex == null) {
            throw new ArgumentNullException("ex");
        }
        if (req == null) {
            throw new ArgumentNullException("req");
        }
        String uri = req.getRequestURI();
        logger.info("validate request failure: uri={}", uri, ex);
        ProcessingMessage msg = ex.getProcessingMessage();
        ArrayNode entity = this.mapper.createArrayNode();
        addObject(uri, entity, msg);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(entity);
    }
}
