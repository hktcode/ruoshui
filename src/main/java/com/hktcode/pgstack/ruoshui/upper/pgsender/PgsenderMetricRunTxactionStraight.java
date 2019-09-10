/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunTxactionStraight extends PgsenderMetricRunTxaction
{
    static PgsenderMetricRunTxactionStraight of(PgActionReplTxactionStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTxactionStraight(action);
    }

    private PgsenderMetricRunTxactionStraight(PgActionReplTxactionStraight action)
    {
        super(action);
    }
}
