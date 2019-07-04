/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

public class BgMethodPstResultSuccess<T extends SimpleBasicBgWorker<T>> implements BgMethodPstResult<T>
{
    public static <T extends SimpleBasicBgWorker<T>> BgMethodPstResultSuccess<T> of()
    {
        return new BgMethodPstResultSuccess<>();
    }

    private BgMethodPstResultSuccess()
    {
    }
}
