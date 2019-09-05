/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmResultErr //
    implements UpcsmResult, SimpleMethodAllResultEnd<UpcsmAction>
{
    public static UpcsmResultErr of(UpcsmMetricErr metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpcsmResultErr(metric);
    }

    public final UpcsmMetricErr metric;

    private UpcsmResultErr(UpcsmMetricErr metric)
    {
        this.metric = metric;
    }
}
