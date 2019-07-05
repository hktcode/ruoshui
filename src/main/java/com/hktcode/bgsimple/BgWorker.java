/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.bgsimple.method.*;

public interface BgWorker<W extends BgWorker<W, M>, M>
{
    SimpleMethodPstResult<W, M> pst(M metric);

    SimpleMethodPutResult<W, M> put(M metric);

    SimpleMethodGetResult<W, M> get(M metric);

    SimpleMethodDelResult<W, M> del(M metric);
}
