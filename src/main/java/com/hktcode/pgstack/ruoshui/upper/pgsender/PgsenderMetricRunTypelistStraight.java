/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunTypelistStraight extends PgsenderMetricRunTypelist
{
    static PgsenderMetricRunTypelistStraight of(PgsenderActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTypelistStraight(action);
    }

    private PgsenderMetricRunTypelistStraight(PgsenderActionDataTypelistStraight action)
    {
        super(action);
    }
}
