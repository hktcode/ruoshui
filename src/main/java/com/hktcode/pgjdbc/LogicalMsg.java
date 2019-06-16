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
}
