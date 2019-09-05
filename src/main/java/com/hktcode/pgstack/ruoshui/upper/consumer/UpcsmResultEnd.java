/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmResultEnd //
    implements UpcsmResult, SimpleMethodAllResultEnd<UpcsmAction>
{
    public static UpcsmResultEnd of(UpcsmMetricEnd metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpcsmResultEnd(metric);
    }

    public final UpcsmMetricEnd metric;

    private UpcsmResultEnd(UpcsmMetricEnd metric)
    {
        this.metric = metric;
    }
}
