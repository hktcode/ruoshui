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
import com.google.common.primitives.ImmutableLongArray;
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

    /**
     * get TupleData from a {@link ByteBuffer}.
     *
     * @param content the {@link ByteBuffer} where get the tuple data from
     * @return the tuple value list.
     * @throws ArgumentNullException if {@code content} is {@code null}.
     */
    static ImmutableList<JsonNode> getTuple(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }

        List<JsonNode> tuple = new ArrayList<>();
        long columnCount = ((int)content.getShort()) & 0xFFFF;
        for (int i = 0; i < columnCount; ++i) {
            byte b = content.get();
            if (b == 'n') {
                tuple.add(NullNode.instance);
            }
            else if (b == 'u') {
                tuple.add(new ObjectNode(JsonNodeFactory.instance));
            }
            else if (b == 't') {
                int length = content.getInt();
                byte[] x = new byte[length];
                content.get(x);
                String v = new String(x, StandardCharsets.UTF_8);
                tuple.add(TextNode.valueOf(v));
            }
            else {
                throw new LogicalMsgFormatException(ImmutableLongArray.of('n', 'u', 't'), b);
            }
        }
        return ImmutableList.copyOf(tuple);
    }


    /**
     * put the value list string representation into a {@code StringBuilder}.
     *
     * @param builder the {@code StringBuilder} to append the string representation.
     * @param values the tuple value list.
     * @throws ArgumentNullException if {@code builder} or {@code tupleval} is {@code null}
     */
    static void toString(StringBuilder builder, ImmutableList<JsonNode> values)
    {
        if (builder == null) {
            throw new ArgumentNullException("builder");
        }
        if (values == null) {
            throw new ArgumentNullException("values");
        }
        char splitter = '[';
        for (JsonNode c : values) {
            builder.append(splitter);
            builder.append(c.toString());
            splitter = ',';
        }
        builder.append(']');
    }
}
