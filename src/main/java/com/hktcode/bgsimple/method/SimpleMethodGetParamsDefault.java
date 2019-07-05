/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodGetParamsDefault<W extends BgWorker<W, M>, M> //
    implements SimpleMethodGetParams<W, M>
{
    public static <W extends BgWorker<W, M>, M> //
    SimpleMethodGetParamsDefault<W, M> of()
    {
        return new SimpleMethodGetParamsDefault<>();
    }

    @Override
    public SimpleMethodGetResult<W, M> run(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.get(metric);
    }
}
