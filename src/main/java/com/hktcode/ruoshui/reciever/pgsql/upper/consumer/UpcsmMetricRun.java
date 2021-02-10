/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.bgsimple.triple.TripleMetricRun;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

public class UpcsmMetricRun extends TripleMetricRun
{
    public static UpcsmMetricRun of(UpcsmActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UpcsmMetricRun(action);
    }

    public final LogSequenceNumber txactionLsn;

    private UpcsmMetricRun(UpcsmActionRun action)
    {
        super(action);
        this.txactionLsn = action.txactionLsn;
    }
}
