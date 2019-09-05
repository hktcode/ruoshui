/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultRun;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmResultRun //
    implements SimpleMethodAllResultRun<UpcsmAction>
{
    public static UpcsmResultRun of(UpcsmMetricRun metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpcsmResultRun(metric);
    }

    public final UpcsmMetricRun metric;

    private UpcsmResultRun(UpcsmMetricRun metric)
    {
        this.metric = metric;
    }
}
