/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.triple.TripleAction;
import com.hktcode.bgsimple.triple.TripleResult;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

public interface UpcsmAction extends TripleAction<UpcsmAction, UpcsmConfig, UpcsmMetricRun>
{
    default TripleResult<UpcsmAction> pst(LogSequenceNumber lsn) //
        throws InterruptedException
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }

    default TripleResult<UpcsmAction> pst(UpcsmParamsPstSnapshot params) //
        throws InterruptedException
    {
        if (params == null) {
            throw new ArgumentNullException("params");
        }
        return this.get();
    }
}
