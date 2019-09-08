/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.hktcode.jackson.JacksonExceptionHandler;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogSequenceNumberJacksonSerializer;
import org.postgresql.replication.LogSequenceNumber;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * the Spring boot application starter.
 */
@SpringBootApplication
public class RuoshuiConfiguration
{
    /**
     * Json Schema验证异常的处理器.
     *
     * @return JacksonExcpetionHandler对象.
     */
    @Bean
    public JacksonExceptionHandler jsonExceptionHandler()
    {
        return new JacksonExceptionHandler();
    }

    /**
     * 序列化反序列化的{@code ObjectMapper}对象.
     *
     * 添加了{@link LogSequenceNumber}序列化类，所以没有采用Spring Boot默认的方法。
     *
     * @param builder Spring Boot用于创建{@code ObjectMapper}的构造器.
     *
     * @return 配置了{@link LogSequenceNumber}的{@link ObjectMapper}对象.
     * @throws ArgumentNullException if {@code builder} is {@code null}.
     */
    @Primary
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder)
    {
        if (builder == null) {
            throw new ArgumentNullException("builder");
        }

        LogSequenceNumberJacksonSerializer serializer //
            = LogSequenceNumberJacksonSerializer.of();
        return builder.createXmlMapper(false) //
            .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE) //
            .serializerByType(LogSequenceNumber.class, serializer)
            .build();
    }

    /**
     * 序列化反序列化Yaml的{@code YAMLMapper}对象.
     *
     * 添加了{@link LogSequenceNumber}序列化类，所以没有采用Spring Boot默认的方法。
     *
     * @param builder Spring Boot用于创建{@code ObjectMapper}的构造器.
     *
     * @return 配置了{@link LogSequenceNumber}的{@link YAMLMapper}对象.
     * @throws ArgumentNullException if {@code builder} is {@code null}.
     */
    @Bean
    public YAMLMapper jacksonYamlMapper(Jackson2ObjectMapperBuilder builder)
    {
        if (builder == null) {
            throw new ArgumentNullException("builder");
        }

        YAMLMapper mapper = new YAMLMapper();
        LogSequenceNumberJacksonSerializer serializer //
            = LogSequenceNumberJacksonSerializer.of();
        builder.createXmlMapper(false) //
            .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE) //
            .serializerByType(LogSequenceNumber.class, serializer);
        builder.configure(mapper);
        return mapper;
    }

    /**
     * 将yaml消息转换为Http消息（body）的转化器类.
     */
    static final class MappingJackson2YamlHttpMessageConverter //
        extends AbstractJackson2HttpMessageConverter
    {
        /**
         * 构造函数.
         *
         * @param yamlMapper 用于转换的yamlMapper对象.
         */
        MappingJackson2YamlHttpMessageConverter(YAMLMapper yamlMapper)
        {
            super(yamlMapper //
                , MediaType.parseMediaType("application/x-yaml") //
                , MediaType.parseMediaType("text/vnd.yaml") //
                , MediaType.parseMediaType("text/yaml") //
                , MediaType.parseMediaType("text/x-yaml") //
            );
        }
    }

    /**
     * 创建将yaml消息转换为Http消息（body）的转化器bean.
     *
     * @param yamlMapper 用于转换的yamlMapper对象.
     * @throws ArgumentNullException if {@code yamlMapper} is {@code null}.
     */
    @Bean
    public MappingJackson2YamlHttpMessageConverter //
    mappingJackson2YamlHttpMessageConverter(YAMLMapper yamlMapper)
    {
        if (yamlMapper == null) {
            throw new ArgumentNullException("yamlMapper");
        }
        return new MappingJackson2YamlHttpMessageConverter(yamlMapper);
    }
}
