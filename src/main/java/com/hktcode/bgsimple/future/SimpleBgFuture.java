/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.hktcode.bgmethod.BgMethodResult;
import com.hktcode.bgmethod.SimpleBgWorker;

import java.util.concurrent.Future;

public interface SimpleBgFuture //
    /* */< T extends BgMethodResult<G> //
    /* */, G extends SimpleBgWorker<G> //
    /* */> extends Future<T>
{
}
