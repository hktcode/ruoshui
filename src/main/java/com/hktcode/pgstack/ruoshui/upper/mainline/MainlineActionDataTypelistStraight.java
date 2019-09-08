/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class MainlineActionDataTypelistStraight //
    extends MainlineActionDataTypelist
{
    static MainlineActionDataTypelistStraight of
        /* */( MainlineConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        return new MainlineActionDataTypelistStraight(config, status, tqueue);
    }

    private MainlineActionDataTypelistStraight
        /* */( MainlineConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue);
    }

    public MainlineActionReplTxactionStraight txaction()
    {
        return MainlineActionReplTxactionStraight.of(this);
    }

    @Override
    public MainlineMetricRunTypelistStraight toRunMetrics()
    {
        return MainlineMetricRunTypelistStraight.of(this);
    }

    @Override
    public MainlineMetricEndTypelistStraight toEndMetrics()
    {
        return MainlineMetricEndTypelistStraight.of(this);
    }
}

