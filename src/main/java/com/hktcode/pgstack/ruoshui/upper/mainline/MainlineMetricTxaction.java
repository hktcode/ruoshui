/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.time.ZonedDateTime;

class MainlineMetricTxaction extends MainlineMetric
{
    static MainlineMetricTxaction of(ZonedDateTime startMillis)
    {
        if (null == startMillis) {
            throw new ArgumentNullException("startMillis");
        }
        return new MainlineMetricTxaction(startMillis);
    }

    // TODO: 消除这个volatile
    volatile LogSequenceNumber txactionLsn = LogSequenceNumber.INVALID_LSN;

    private MainlineMetricTxaction(ZonedDateTime startMillis)
    {
        super(startMillis);
    }
}
