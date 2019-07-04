/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.bgsimple.method.*;

import java.time.ZonedDateTime;

public interface BgWorker<W extends BgWorker<W, M>, M>
{
    SimpleMethodPstResult<W, M> pst(M metric);

    SimpleMethodPutResult<W, M> put(M metric);

    SimpleMethodGetResult<W, M> get(M metric);

    SimpleMethodDelResult<W, M> del(M metric);
}
