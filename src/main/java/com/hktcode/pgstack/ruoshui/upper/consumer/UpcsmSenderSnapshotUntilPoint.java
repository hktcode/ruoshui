/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class UpcsmSenderSnapshotUntilPoint extends UpcsmSenderSnapshot
{
    private static final Logger logger //
        = LoggerFactory.getLogger(UpcsmSenderSnapshotUntilPoint.class);

    public static UpcsmSenderSnapshotUntilPoint of
        /* */(UpcsmSenderSnapshot snapshot //
        /* */, PgReplSlotTuple slttuple //
        /* */) //
    {
        if (snapshot == null) {
            throw new ArgumentNullException("snapshot");
        }
        if (slttuple == null) {
            throw new ArgumentNullException("slttuple");
        }
        return new UpcsmSenderSnapshotUntilPoint(snapshot, slttuple);
    }

    public final PgReplSlotTuple slot;

    private UpcsmSenderSnapshotUntilPoint //
        /* */( UpcsmSenderSnapshot snapshot //
        /* */, PgReplSlotTuple slttuple //
        /* */) //
    {
        super(snapshot);
        this.slot = slttuple;
    }

    @Override
    public UpperRecordConsumer poll(long timeout, UpcsmActionRun action) //
        throws InterruptedException
    {
        PgRecord record = mlxact.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            return record.toRecord(action, this);
        }
        return super.pollDefaultRecord(action);
    }
}
