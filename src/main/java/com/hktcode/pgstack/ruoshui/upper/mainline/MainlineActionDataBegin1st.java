/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

abstract class MainlineActionDataBegin1st<C extends MainlineConfig> //
    extends MainlineActionData<C>
{
    public final long startMillis;

    MainlineActionDataBegin1st //
        /* */( C config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */, long actionStart //
        /* */)
    {
        super(config, status, tqueue, actionStart);
        this.startMillis = actionStart;
    }

    @Override
    public MainlineMetricRunBegin1st toRunMetrics()
    {
        return MainlineMetricRunBegin1st.of(this);
    }

    @Override
    public MainlineMetricEndBegin1st toEndMetrics()
    {
        return MainlineMetricEndBegin1st.of(this);
    }
}
