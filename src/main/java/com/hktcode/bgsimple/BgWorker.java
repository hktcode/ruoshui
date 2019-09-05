/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.SimpleStatusInner;

public interface BgWorker<W extends BgWorker<W>>
{
    SimpleMethodPstResult<W> pst() throws InterruptedException;

    SimpleMethodPutResult<W> put() throws InterruptedException;

    SimpleMethodGetResult<W> get() throws InterruptedException;

    SimpleMethodDelResult<W> del() throws InterruptedException;

    SimpleStatusInner newStatus(W wkstep) throws InterruptedException;
}
