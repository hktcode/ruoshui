/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricRunTxactionStraight extends MainlineMetricRunTxaction
{
    static MainlineMetricRunTxactionStraight of(MainlineActionReplTxactionStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricRunTxactionStraight(action);
    }

    private MainlineMetricRunTxactionStraight(MainlineActionReplTxactionStraight action)
    {
        super(action);
    }
}
