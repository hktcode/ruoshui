/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.method.SimpleMethodParams;
import com.hktcode.bgsimple.method.SimpleMethodResult;
import com.hktcode.lang.exception.ArgumentIllegalException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

public class UpcsmParamsPstRecvLsn implements SimpleMethodParams
{
    public static UpcsmParamsPstRecvLsn of(LogSequenceNumber receiveLsn)
    {
        if (receiveLsn == null) {
            throw new ArgumentNullException("receiveLsn");
        }
        return new UpcsmParamsPstRecvLsn(receiveLsn);
    }

    private final LogSequenceNumber receiveLsn;

    private UpcsmParamsPstRecvLsn(LogSequenceNumber receiveLsn)
    {
        this.receiveLsn = receiveLsn;
    }

    @Override
    public SimpleMethodResult run(BgWorker worker) //
        throws InterruptedException
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (!(worker instanceof UpcsmActionRun)) {
            throw new ArgumentIllegalException("worker type wrong", "worker", worker);
        }
        return ((UpcsmActionRun)worker).pst(this.receiveLsn);
    }
}
