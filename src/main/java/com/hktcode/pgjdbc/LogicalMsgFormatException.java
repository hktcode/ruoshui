/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.google.common.primitives.ImmutableLongArray;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 解析Logical Replication Message发生错误时抛出的异常.
 */
public class LogicalMsgFormatException extends RuntimeException
{
    /**
     * 期待的字节列表.
     *
     * 虽然这里是采用了{@code long}数组，但是其实是字节信息.
     */
    public final ImmutableLongArray expect;

    /**
     * 真实的字节信息.
     *
     * 虽然这里采用了{@code long}类项变量，不过其实是字节.
     */
    public final long actual;

    /**
     * 构造函数.
     *
     * @param expect 期待的字节列表.
     * @param actual 真实解析时遇到的字节列表.
     */
    public LogicalMsgFormatException(ImmutableLongArray expect, long actual)
    {
        super(formatMessage(expect, actual));
        if (expect == null) {
            throw new ArgumentNullException("expect");
        }
        this.expect = expect;
        this.actual = actual;
    }

    /**
     * 构建异常消息.
     *
     * @param expect 期待的字节列表.
     * @param actual 真实遇到的字节信息.
     * @return 描述异常发生原因的字符串.
     */
    private static String formatMessage(ImmutableLongArray expect, long actual)
    {
        String format = "pgoutput message format is wrong: expect=%s, actual=0x%2x";
        return String.format(format, expect == null ? "[]" : expect , actual);
    }
}
