/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodPstParamsDefault<W extends BgWorker<W, M>, M> //
    implements SimpleMethodPstParams<W, M>
{
    public static <W extends BgWorker<W, M>, M> //
    SimpleMethodPstParamsDefault<W, M> of()
    {
        return new SimpleMethodPstParamsDefault<>();
    }

    private SimpleMethodPstParamsDefault()
    {
    }

    @Override
    public SimpleMethodPstResult<W, M> run(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return worker.pst(metric);
    }
}
