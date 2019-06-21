/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import org.postgresql.replication.LogSequenceNumber;

import java.time.ZonedDateTime;

public class UpperMetric
{
    public static UpperMetric of()
    {
        return new UpperMetric();
    }

    public final ZonedDateTime createTime;


    public long sleepedCount = 0;

    public long/*   */pushMillis = 0;

    public long/*   */logDuration = 10 * 1000;
    public long/*   */recordSize = 0;
    public boolean/**/getSnapshot = false;

    public long pushCounts = 0;

    public long sleepedMillis = 0;

    public long commitedLsnCount = 0;

    public long lastReceiveLsn = LogSequenceNumber.INVALID_LSN.asLong();

    public long lastLoggerMillis = 0;

    public long untillsn = -1;

    private UpperMetric()
    {
        this.createTime = ZonedDateTime.now();
    }
}
