/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperThreadThrowsException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

import java.time.ZonedDateTime;

class PgsqlTxactionRecordThrows implements PgsqlTxactionRecord
{
    static PgsqlTxactionRecordThrows of(ZonedDateTime endtime, Throwable throwable)
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        return new PgsqlTxactionRecordThrows(endtime, throwable);
    }

    private final ZonedDateTime endtime;

    private final Throwable throwable;

    @Override
    public UpperConsumerRecord update(UpperConsumerMetric metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        throw new UpperThreadThrowsException("datatype", throwable);
    }

    private PgsqlTxactionRecordThrows(ZonedDateTime endtime, Throwable throwable)
    {
        this.endtime = endtime;
        this.throwable = throwable;
    }
}
