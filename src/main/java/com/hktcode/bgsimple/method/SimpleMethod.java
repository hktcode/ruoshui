package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethod
{
    SimpleMethodResult run(BgWorker wkstep) throws InterruptedException;
}
