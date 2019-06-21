/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;
import org.postgresql.replication.LogSequenceNumber;

import java.time.ZonedDateTime;

public class UpperTxactionMetric extends UpperRunnableMetric
{
    public static UpperTxactionMetric of(ZonedDateTime startMillis)
    {
        if (null == startMillis) {
            throw new ArgumentNullException("startMillis");
        }
        return new UpperTxactionMetric(startMillis);
    }

    // TODO: 消除这个volatile
    public volatile LogSequenceNumber txactionLsn = LogSequenceNumber.INVALID_LSN;

    private UpperTxactionMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
    }
}
