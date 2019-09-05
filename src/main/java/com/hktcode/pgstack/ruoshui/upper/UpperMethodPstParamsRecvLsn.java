/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.bgsimple.method.SimpleMethodPst;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmAction;
import org.postgresql.replication.LogSequenceNumber;

public class UpperMethodPstParamsRecvLsn implements SimpleMethodPst<UpcsmAction>
{
    public static UpperMethodPstParamsRecvLsn of(LogSequenceNumber receiveLsn)
    {
        if (receiveLsn == null) {
            throw new ArgumentNullException("receiveLsn");
        }
        return new UpperMethodPstParamsRecvLsn(receiveLsn);
    }

    private final LogSequenceNumber receiveLsn;

    private UpperMethodPstParamsRecvLsn(LogSequenceNumber receiveLsn)
    {
        this.receiveLsn = receiveLsn;
    }

    @Override
    public SimpleMethodPstResult<UpcsmAction> run(UpcsmAction worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        // TODO: return worker.pst(this.receiveLsn);
        return null;
    }
}
