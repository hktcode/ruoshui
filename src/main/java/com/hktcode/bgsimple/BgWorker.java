/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.SimpleStatusInner;

public interface BgWorker<W extends BgWorker<W>>
{
    SimpleMethodPstResult<W> pst();

    SimpleMethodPutResult<W> put();

    SimpleMethodGetResult<W> get();

    SimpleMethodDelResult<W> del();

    SimpleStatusInner newStatus(W wkstep) throws InterruptedException;
}
