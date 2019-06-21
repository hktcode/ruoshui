/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 委托运行任务的线程失败时抛出的异常.
 */
public class UpperThreadThrowsException extends RuntimeException
{
    /**
     * 构造函数.
     *
     * @param type 运行任务的类型.
     * @param throwable 运行任务内部线程抛出的异常.
     * @throws ArgumentNullException if {@code type} or {@code throwable} is {@code null}.
     */
    public UpperThreadThrowsException(String type, Throwable throwable)
    {
        super(buildMessage(type, throwable), throwable);
        if (type == null) {
            throw new ArgumentNullException("type");
        }
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
    }

    /**
     * 构建异常消息.
     *
     * @param type 运行任务的类型.
     * @param throwable 运行任务内部线程抛出的异常.
     *
     * @return 根据{@code type}和[@code throwable}构造的{@link UpperThreadThrowsException}消息内容.
     */
    private static String buildMessage(String type, Throwable throwable)
    {
        type = (type == null ? "" : type);
        String msg = "";
        if (throwable != null) {
            msg = throwable.getMessage();
        }
        return type + " throws exception: " + msg;
    }
}
