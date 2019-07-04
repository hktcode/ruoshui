/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.bgmethod.BgMethodPstParams;
import com.hktcode.bgmethod.BgMethodPstResult;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.time.ZonedDateTime;

public class UpperLastReceiveLsnPstParams implements BgMethodPstParams<UpperConsumer>
{
    public static UpperLastReceiveLsnPstParams of(LogSequenceNumber lastReceiveLsn)
    {
        if (lastReceiveLsn == null) {
            throw new ArgumentNullException("lastReceive");
        }
        return new UpperLastReceiveLsnPstParams(lastReceiveLsn);
    }

    private final LogSequenceNumber lastReceiveLsn;

    private UpperLastReceiveLsnPstParams(LogSequenceNumber lastReceiveLsn)
    {
        this.lastReceiveLsn = lastReceiveLsn;
    }

    @Override
    public BgMethodPstResult<UpperConsumer> run(UpperConsumer worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.pst(this.lastReceiveLsn);
    }

    @Override
    public BgMethodPstResult<UpperConsumer> run(UpperConsumer worker, Throwable reasons, ZonedDateTime endtime)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return worker.pst(reasons, endtime);
    }
}
