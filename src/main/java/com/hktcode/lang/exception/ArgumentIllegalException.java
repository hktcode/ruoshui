/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.lang.exception;

/**
 * 参数不合法时抛出的异常.
 *
 * <p>
 * 如果不合法的参数值为{@code null}，那么应该抛出{@code ArgumentNullException}异常.
 * </p>
 *
 * TODO: 说明为什么{@code ArgumentNullException}没有继承{@code ArgumentIllegalException}.
 *
 * @see com.hktcode.lang.exception.ArgumentNullException
 */
public class ArgumentIllegalException extends IllegalArgumentException
{
    /**
     * 参数名称.
     */
    public final String name;

    /**
     * 不合法的参数值.
     */
    public final Object value;

    /**
     * 构造函数.
     *
     * @param desc 描述信息.
     * @param name 参数名称.
     * @param value 不合法的参数值.
     *
     * @throws ArgumentNullException 当三个参数中任何一个为{@code null}时抛出.
     */
    public ArgumentIllegalException(String desc, String name, Object value)
    {
        super(buildMessage(desc, name, value));
        if (desc == null) {
            throw new ArgumentNullException("desc");
        }
        if (name == null) {
            throw new ArgumentNullException("name");
        }
        if (value == null) {
            throw new ArgumentNullException("value");
        }
        this.name = name;
        this.value = value;
    }

    /**
     * 构造异常消息.
     *
     * @param desc 描述信息. 如果是{@code null}，则用{@code "argument is illegal"}代替.
     * @param name 参数名称. 如果是{@code null}，则用空字符串（{@code ""}）代替.
     * @param value 不合法的参数值. 如果是{@code null}，则用字符串{@code "null"}代替.
     *
     * @return 以{@code "$description: name=$name, value=$value"}表示的异常消息.
     */
    private static String buildMessage(String desc, String name, Object value)
    {
        desc = (desc == null ? "argument is illegal" : desc);
        name = (name == null ? "" : name);
        value = (value == null ? "null" : value);
        return String.format("%s: name=%s, value=%s", desc, name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "ArgumentIllegalException: " + this.getMessage();
    }
}
