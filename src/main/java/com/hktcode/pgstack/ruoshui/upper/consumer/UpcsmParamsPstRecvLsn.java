/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodPst;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

public class UpcsmParamsPstRecvLsn implements SimpleMethodPst<UpcsmActionRun>
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
    public SimpleMethodPstResult<UpcsmActionRun> run(UpcsmActionRun worker) //
        throws InterruptedException
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.pst(this.receiveLsn);
    }
}
