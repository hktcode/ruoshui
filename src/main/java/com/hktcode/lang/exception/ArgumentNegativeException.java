/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.lang.exception;

/**
 * 参数为负数时抛出的异常.
 */
public class ArgumentNegativeException extends ArgumentIllegalException
{
    /**
     * 构造函数.
     *
     * @param name 参数名称.
     * @param value 不合法的参数值.
     *
     * @throws ArgumentNullException 当两个参数中任一一个为{@code null}时抛出.
     */
    public ArgumentNegativeException(String name, Number value)
    {
        super("argument is negative", name == null ? "" : name, value == null ? "" : value);
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (value == null) {
            throw new ArgumentNullException("value");
        }
        // TODO: 检查Number是否是负数.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "ArgumentNegativeException: name=" + super.name + ", value=" + super.value;
    }
}
