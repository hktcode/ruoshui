/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmActionRun;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmSenderSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmSenderSnapshotSimpleData;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmSenderSnapshotUntilPoint;

public class PgRecordExecFinish implements PgRecord
{
    public static PgRecordExecFinish of(PgResultEnd result)
    {
        if (result == null) {
            throw new ArgumentNullException("result");
        }
        return new PgRecordExecFinish(result);
    }

    private final PgResultEnd result;

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshotSimpleData sender)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        sender.mlxact.sslist.add(result);
        action.fetchThread = sender.mlxact;
        return sender.record;
    }

    private PgRecordExecFinish(PgResultEnd result)
    {
        this.result = result;
    }
}
