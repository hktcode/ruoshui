/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

public class BgMethodPstResultSuccess<T extends SimpleBgWorker<T>> implements BgMethodPstResult<T>
{
    public static <T extends SimpleBgWorker<T>> BgMethodPstResultSuccess<T> of()
    {
        return new BgMethodPstResultSuccess<>();
    }

    private BgMethodPstResultSuccess()
    {
    }
}
