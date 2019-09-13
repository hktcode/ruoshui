/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.triple.TripleMetricRun;
import com.hktcode.lang.exception.ArgumentNullException;

public class UppdcMetricRun extends TripleMetricRun
{
    public static UppdcMetricRun of(UppdcActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UppdcMetricRun(action);
    }

    private UppdcMetricRun(UppdcActionRun action)
    {
        super(action);
    }
}
