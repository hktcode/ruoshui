/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricRunTypelistStraight extends MainlineMetricRunTypelist
{
    static MainlineMetricRunTypelistStraight of(MainlineActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricRunTypelistStraight(action);
    }

    private MainlineMetricRunTypelistStraight(MainlineActionDataTypelistStraight action)
    {
        super(action);
    }
}
