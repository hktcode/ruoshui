/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodDelParamsDefault<W extends BgWorker<W, M>, M> //
    implements SimpleMethodDelParams<W, M>
{
    public static <W extends BgWorker<W, M>, M>
    SimpleMethodDelParamsDefault<W, M> of()
    {
        return new SimpleMethodDelParamsDefault<>();
    }

    private SimpleMethodDelParamsDefault()
    {
    }

    @Override
    public SimpleMethodDelResult<W, M> run(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.del(metric);
    }
}
