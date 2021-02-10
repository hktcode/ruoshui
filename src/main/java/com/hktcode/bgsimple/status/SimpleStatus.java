/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleStatus
{
    SimpleStatusCmd cmd(SimpleStatusCmd cmd);
    SimpleStatus run(BgWorker wkstep, int number) throws InterruptedException;
}
