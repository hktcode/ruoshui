/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import org.postgresql.replication.LogSequenceNumber;

class MainlineMetricNormalTxaction extends MainlineMetricNormal
{
    static MainlineMetricNormalTxaction of(long startMillis, long actionStart)
    {
        return new MainlineMetricNormalTxaction(startMillis, actionStart);
    }

    // TODO: @Deprecated
    public long getrsMillis = -1;

    // TODO: @Deprecated
    public long maxnextTime = -1;

    public final long actionStart;

    public LogSequenceNumber txactionLsn = LogSequenceNumber.INVALID_LSN;

    private MainlineMetricNormalTxaction
        /* */( long startMillis
        /* */, long actionStart
        /* */)
    {
        super(startMillis);
        this.actionStart = actionStart;
    }
}
