/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.lang;

import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 应用程序帮助类.
 */
public class Application
{
    /**
     * 获取{@code HOME}环境变量的值.
     *
     * <p>
     * 本方法会通过方法{@link System#getenv(String)}获取参数{@code homeEnvName}的值.
     * </p>
     *
     * <p>
     * 如果{@link System#getenv(String)}方法返回{@code null}，
     * 那么本方法会向标准错误中输出一条消息，然后使用{@code -1}参数调用
     * {@link System#exit(int)}退出程序.
     * </p>
     *
     * @param homeEnvName {@code HOME}环境变量的名称.
     * @return {@code HOME}环境变量的值.
     * @throws ArgumentNullException 如果参数为{@code null}时抛出.
     */
    public static String getHome(String homeEnvName)
    {
        if (homeEnvName == null) {
            throw new ArgumentNullException("homeEnvName");
        }
        // TODO: "".equals(homeEnvName) throw Exception
        final String home = System.getenv(homeEnvName);
        if (home == null) {
            System.err.printf("HOME ENVIROMENT IS UNDEFINED: name=%s%n", homeEnvName);
            System.exit(-1);
        }
        return home;
    }

    /**
     * 构造函数.
     *
     * 将默认构造函数设置为private只是为迎合SonarLint。
     */
    private Application()
    {
    }
}
