/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;

import java.time.ZonedDateTime;

/**
 * 发送相关信息给外部调用者.
 *
 * TODO: 此方法需要重构.
 *
 * @param <M> 快照的metric类.
 */
public interface PgSnapshotSender<M>
{
    M snapshotMetric(ZonedDateTime startMillis);

    void sendStatusInfo(String statusInfo, M metric);

    void sendCreateSlot(PgReplSlotTuple slotTuple, long timeout, M metric)
        throws InterruptedException;

    void sendExecFinish(long timeout, M metric) throws InterruptedException;

    void sendExecThrows(Throwable throwable, long timeout, M metric)
        throws InterruptedException;

    void sendLogicalMsg(long lsn, LogicalMsg msg, long timeout, M metric)
        throws InterruptedException;

    void sendPauseWorld(long timeout, M metric) throws InterruptedException;
}
