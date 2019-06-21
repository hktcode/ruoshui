/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperThreadThrowsException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

import java.time.ZonedDateTime;

public class UpperTxactionRecordThrows implements UpperTxactionRecord
{
    public static UpperTxactionRecordThrows of(ZonedDateTime endtime, Throwable throwable)
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        return new UpperTxactionRecordThrows(endtime, throwable);
    }

    public final ZonedDateTime endtime;

    private final Throwable throwable;

    @Override
    public UpperConsumerRecord update(UpperConsumerMutableMetric metric)
    {
        throw new UpperThreadThrowsException("txaction", throwable);
    }

    private UpperTxactionRecordThrows(ZonedDateTime endtime, Throwable throwable)
    {
        this.endtime = endtime;
        this.throwable = throwable;
    }
}
