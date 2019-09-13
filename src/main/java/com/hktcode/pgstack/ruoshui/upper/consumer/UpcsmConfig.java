/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgConfigMainline;

public class UpcsmConfig extends TqueueConfig
{
    public static UpcsmConfig of(PgConfigMainline mainlineCfg)
    {
        if (mainlineCfg == null) {
            throw new ArgumentNullException("mainlineCfg");
        }
        return new UpcsmConfig(mainlineCfg);
    }

    public final PgConfigMainline mainlineCfg;

    private UpcsmConfig(PgConfigMainline mainlineCfg)
    {
        this.mainlineCfg = mainlineCfg;
    }
}
