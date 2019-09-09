/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.method.SimpleMethodPst;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderAction;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderResult;
import org.postgresql.replication.LogSequenceNumber;

public class MainlineMethodPstParamsRecvLsn implements SimpleMethodPst<PgsenderAction<MainlineRecord, MainlineConfig>>
{
    public static MainlineMethodPstParamsRecvLsn of(LogSequenceNumber receiveLsn)
    {
        if (receiveLsn == null) {
            throw new ArgumentNullException("receiveLsn");
        }
        return new MainlineMethodPstParamsRecvLsn(receiveLsn);
    }

    private final LogSequenceNumber receiveLsn;

    private MainlineMethodPstParamsRecvLsn(LogSequenceNumber receiveLsn)
    {
        this.receiveLsn = receiveLsn;
    }

    @Override
    public PgsenderResult<MainlineRecord, MainlineConfig> run(PgsenderAction<MainlineRecord, MainlineConfig> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return action.pst(this.receiveLsn);
    }
}
