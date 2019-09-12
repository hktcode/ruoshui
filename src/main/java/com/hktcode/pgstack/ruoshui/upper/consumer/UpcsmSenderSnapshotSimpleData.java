/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgConfig;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord;

import java.util.concurrent.TimeUnit;

public class UpcsmSenderSnapshotSimpleData extends UpcsmSenderSnapshot
{
    public static UpcsmSenderSnapshotSimpleData of(UpcsmSenderSnapshot snapshot)
    {
        if (snapshot == null) {
            throw new ArgumentNullException("snapshot");
        }
        return new UpcsmSenderSnapshotSimpleData(snapshot);
    }

    public static UpcsmSenderSnapshotSimpleData of
        /* */( UpcsmSenderSnapshot thread //
        /* */, UpperRecordConsumer record //
        /* */) //
    {
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        return new UpcsmSenderSnapshotSimpleData(thread, record);
    }

    public static UpcsmSenderSnapshotSimpleData of //
        /* */(UpcsmSenderMainline mlxact //
        /* */, PgConfig config //
        /* */) //
    {
        if (mlxact == null) {
            throw new ArgumentNullException("mlxact");
        }
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new UpcsmSenderSnapshotSimpleData(mlxact, config);
    }

    private UpcsmSenderSnapshotSimpleData(UpcsmSenderMainline mlxact, PgConfig config)
    {
        super(mlxact, config);
    }

    private UpcsmSenderSnapshotSimpleData
        /* */( UpcsmSenderSnapshot thread //
        /* */, UpperRecordConsumer record //
        /* */) //
    {
        super(thread, record);
    }

    private UpcsmSenderSnapshotSimpleData(UpcsmSenderSnapshot snapshot)
    {
        super(snapshot);
    }

    @Override
    public UpperRecordConsumer poll(long timeout, UpcsmActionRun action)
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        PgRecord r = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (r != null) {
            return r.toRecord(action, this);
        }
        return this.pollDefaultRecord(action);
    }
}
