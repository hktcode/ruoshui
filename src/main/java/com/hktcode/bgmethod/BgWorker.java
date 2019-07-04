/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgWorker<W extends BgWorker<W, M>, M>
{
    void pst(M metric);

    void put(M metric);

    void get(M metric);

    void del(M metric);

    void del(M metric, Throwable reason, ZonedDateTime endtime);
}
