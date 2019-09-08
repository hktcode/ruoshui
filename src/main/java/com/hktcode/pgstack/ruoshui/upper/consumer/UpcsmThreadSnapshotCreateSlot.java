/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class UpcsmThreadSnapshotCreateSlot extends UpcsmThreadSnapshot
{
    public static UpcsmThreadSnapshotCreateSlot of(UpcsmThreadSnapshot snapshot)
    {
        if (snapshot == null) {
            throw new ArgumentNullException("snapshot");
        }
        return new UpcsmThreadSnapshotCreateSlot(snapshot);
    }

    private UpcsmThreadSnapshotCreateSlot(UpcsmThreadSnapshot snapshot)
    {
        super(snapshot);
    }

    private static final Logger logger = LoggerFactory.getLogger(UpcsmThreadSnapshotCreateSlot.class);

    @Override
    public UpperConsumerRecord poll(long timeout, UpcsmActionRun action)
        throws InterruptedException
    {
        UpcsmFetchRecordSnapshot record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (   (record instanceof UpcsmFetchRecordSnapshotCreateSlot)
            || (record instanceof UpcsmFetchRecordSnapshotExecThrows)
            || (record instanceof UpcsmFetchRecordSnapshotExecFinish)
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
