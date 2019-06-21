/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.post;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThreadBasic;
import com.hktcode.pgstack.ruoshui.upper.txaction.UpperTxactionThread;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.TransferQueue;

public abstract class UpperSnapshotPostThread extends UpperConsumerThreadBasic<UpperSnapshotPostRecord>
{
    protected final UpperTxactionThread xact;

    protected UpperSnapshotPostThread
        /* */( Thread thread
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue
        /* */, UpperTxactionThread xact
        /* */)
    {
        super(thread, tqueue);
        this.xact = xact;
    }

    @Override
    public void setTxactionLsn(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        this.xact.setTxactionLsn(lsn);
    }
}
