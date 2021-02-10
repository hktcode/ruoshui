/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.exception.JsonSchemaValidationException;
import com.hktcode.jackson.exception.JsonSchemaValidationImplException;
import com.hktcode.jackson.exception.JsonSchemaValidationProcessingException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Jackson序列化库的异常Handler.
 *
 * TODO: 编写文档说明各个异常转换成的Json对象的各个字段，或者编写JsonSchema说明.
 */
@ControllerAdvice
public class HttpStatusJacksonObjectExceptionHandler
{
    private final static Logger logger = LoggerFactory.getLogger(HttpStatusJacksonObjectExceptionHandler.class);

    public static HttpStatusJacksonObjectExceptionHandler of(ObjectMapper mapper)
    {
        if (mapper == null) {
            throw new ArgumentNullException("mapper");
        }
        return new HttpStatusJacksonObjectExceptionHandler(mapper);
    }

    private final ObjectMapper mapper;

    private HttpStatusJacksonObjectExceptionHandler(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }

    /**
     * 处理{@link JacksonObjectException}异常.
     *
     * @param ex 要处理的{@link JacksonObjectException}异常.
     * @param req 访问的请求.
     * @return {@link JacksonObjectException}转换成的响应对象.
     */
    @ExceptionHandler
    public ResponseEntity<ArrayNode> jacksonObjectException(HttpStatusJacksonObjectException ex, HttpServletRequest req)
    {
        if (ex == null) {
            throw new ArgumentNullException("ex");
        }
        if (req == null) {
            throw new ArgumentNullException("req");
        }
        String uri = req.getRequestURI();
        if (ex instanceof JsonSchemaValidationImplException) {
            JsonSchemaValidationImplException e = (JsonSchemaValidationImplException)ex;
            logger.info("validate request failure: uri={}\n{}", uri, e.report, ex);
        }
        else if (ex instanceof JsonSchemaValidationProcessingException) {
            JsonSchemaValidationProcessingException e = (JsonSchemaValidationProcessingException)ex;
            logger.info("validate request failure: uri={}\n{}", uri, e.getProcessingMessage(), ex);
        }
        else if (ex instanceof JsonSchemaValidationException) {
            logger.info("validate request failure: uri={}", uri, ex);
        }
        ArrayNode result = this.mapper.createArrayNode();
        ObjectNode entity = result.addObject();
        ex.toJsonObject(entity);
        return ResponseEntity.status(ex.code).body(result);
    }
}
