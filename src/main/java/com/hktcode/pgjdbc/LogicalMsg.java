/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.ImmutableIntArray;
import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 表示逻辑复制流中的消息.
 */
public interface LogicalMsg
{
    /**
     * 根据PostgreSQL流复制协议，从{@link ByteBuffer}中读取一个C字符串.
     *
     * @param content 要读取的{@link ByteBuffer}
     *
     * @return 指定的字符串信息.
     * @throws ArgumentNullException if the {@code content} argument is {@code null}
     */
    static String readCStyleUtf8String(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        int position = content.position();
        int length = 0;
        while (content.get(position + length) != 0) {
            ++length;
        }
        byte[] bytes = new byte[length];
        content.position(position);
        content.get(bytes);
        content.get();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
