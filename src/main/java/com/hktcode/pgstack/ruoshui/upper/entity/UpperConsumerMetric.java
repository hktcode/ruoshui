/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hktcode.bgsimple.triple.TripleConsumerMetric;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionInfo;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThread;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThreadNoop;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineMetric;

import java.time.ZonedDateTime;

public class UpperConsumerMetric extends TripleConsumerMetric
{
    public static UpperConsumerMetric of(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new UpperConsumerMetric(startMillis);
    }

    private UpperConsumerMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
    }

    public PgConnectionInfo pgreplInfor;

    @JsonIgnore
    public UpperConsumerThread fetchThread = UpperConsumerThreadNoop.of(); // TODO:

    public MainlineMetric txactMetric = null; // TODO:
}
