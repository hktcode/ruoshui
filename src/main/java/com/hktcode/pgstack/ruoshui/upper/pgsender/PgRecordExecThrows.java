/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.*;

public class PgRecordExecThrows implements PgRecord
{
    public static PgRecordExecThrows of(PgResultErr result)
    {
        if (result == null) {
            throw new ArgumentNullException("result");
        }
        return new PgRecordExecThrows(result);
    }

    public final PgResultErr result;

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshotSimpleData sender)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        sender.mlxact.sslist.add(this.result);
        action.fetchThread = sender.mlxact;
        return null;
    }

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshotUntilPoint sender)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        throw new FetchThreadThrowsErrorException();
    }

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderMainline thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        throw new FetchThreadThrowsErrorException();
    }

    private PgRecordExecThrows(PgResultErr result)
    {
        this.result = result;
    }
}
