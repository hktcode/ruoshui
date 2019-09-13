/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.triple.TripleActionEnd;
import com.hktcode.bgsimple.triple.TripleMetricEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmActionEnd extends TripleActionEnd<UpcsmAction, UpcsmConfig, UpcsmMetricRun>
    implements UpcsmAction
{
    public static UpcsmActionEnd of(UpcsmActionRun action) throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        UpcsmMetricRun basicMetric = action.get().metric;
        TripleMetricEnd<UpcsmMetricRun> metric = TripleMetricEnd.of(basicMetric);
        return new UpcsmActionEnd(action, metric);
    }

    private UpcsmActionEnd(UpcsmActionRun action, TripleMetricEnd<UpcsmMetricRun> metric)
    {
        super(action, action.config, metric, 0);
    }
}
