/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecordExecThrows;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecordExecFinish;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecordCreateSlot;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class UpcsmSenderSnapshotCreateSlot extends UpcsmSenderSnapshot
{
    public static UpcsmSenderSnapshotCreateSlot of(UpcsmSenderSnapshot snapshot)
    {
        if (snapshot == null) {
            throw new ArgumentNullException("snapshot");
        }
        return new UpcsmSenderSnapshotCreateSlot(snapshot);
    }

    private UpcsmSenderSnapshotCreateSlot(UpcsmSenderSnapshot snapshot)
    {
        super(snapshot);
    }

    private static final Logger logger = LoggerFactory.getLogger(UpcsmSenderSnapshotCreateSlot.class);

    @Override
    public UpperRecordConsumer poll(long timeout, UpcsmActionRun action)
        throws InterruptedException
    {
        PgRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (   (record instanceof PgRecordCreateSlot)
            || (record instanceof PgRecordExecThrows)
            || (record instanceof PgRecordExecFinish)
        ) {
            return record.toRecord(action, this);
        }
        else if (thread.isAlive()) {
            logger.error("should never happen");// TODO:
        }
        else {
            logger.error("snapshot post is not alive."); // TODO:
            // TODO: mlxact.sslist.add();
            action.fetchThread = mlxact;
        }
        return null;
    }
}
