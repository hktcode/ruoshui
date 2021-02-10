package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethod<W extends BgWorker<W>>
{
    SimpleMethodResult<W> run(W wkstep) throws InterruptedException;
}
