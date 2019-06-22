/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.bgtriple.naive.NaiveConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalStreamStarter;

public class UpperTxactionConfig
{
    public static UpperTxactionConfig of(LogicalStreamStarter logicalRepl)
    {
        if (logicalRepl == null) {
            throw new ArgumentNullException("logicalRepl");
        }
        return new UpperTxactionConfig(logicalRepl);
    }

    public final LogicalStreamStarter logicalRepl;

    public long waitTimeout = NaiveConfig.DEFALUT_WAIT_TIMEOUT;

    public long logDuration = NaiveConfig.DEFAULT_LOG_DURATION;

    private UpperTxactionConfig(LogicalStreamStarter logicalRepl)
    {
        this.logicalRepl = logicalRepl;
    }
}
