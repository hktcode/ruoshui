/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodPutParamsDefault<W extends BgWorker<W, M>, M> //
    implements SimpleMethodPutParams<W, M>
{
    public static <W extends BgWorker<W, M>, M>
    SimpleMethodPutParamsDefault<W, M> of()
    {
        return new SimpleMethodPutParamsDefault<>();
    }

    private SimpleMethodPutParamsDefault()
    {
    }

    @Override
    public SimpleMethodPutResult<W, M> run(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.put(metric);
    }
}
