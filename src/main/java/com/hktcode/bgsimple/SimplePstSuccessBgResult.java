/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

public class SimplePstSuccessBgResult<T extends SimpleBasicBgWorker<T>> implements SimpleBasicPstBgResult<T>
{
    public static <T extends SimpleBasicBgWorker<T>> SimplePstSuccessBgResult<T> of()
    {
        return new SimplePstSuccessBgResult<>();
    }

    private SimplePstSuccessBgResult()
    {
    }
}
