/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hktcode.bgtriple.naive.NaiveConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionInfo;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThread;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThreadNoop;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineMetric;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UpperConsumerMutableMetric extends NaiveConsumerMutableMetric
{
    public static UpperConsumerMutableMetric of()
    {
        ZonedDateTime createTime = ZonedDateTime.now();
        return new UpperConsumerMutableMetric(createTime);
    }

    private UpperConsumerMutableMetric(ZonedDateTime createTime)
    {
        super(createTime);
    }

    public PgConnectionInfo pgreplInfor;

    @JsonIgnore
    public UpperConsumerThread fetchThread = UpperConsumerThreadNoop.of(); // TODO:

    public MainlineMetric txactMetric = null; // TODO:
}
