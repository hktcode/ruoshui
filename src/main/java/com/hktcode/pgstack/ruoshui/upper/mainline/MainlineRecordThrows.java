/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperThreadThrowsException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

import java.time.ZonedDateTime;

class MainlineRecordThrows implements MainlineRecord
{
    static MainlineRecordThrows of(ZonedDateTime endtime, Throwable throwable)
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        return new MainlineRecordThrows(endtime, throwable);
    }

    private final ZonedDateTime endtime;

    private final Throwable throwable;

    @Override
    public UpperConsumerRecord update(UpperConsumerMutableMetric metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        throw new UpperThreadThrowsException("datatype", throwable);
    }

    private MainlineRecordThrows(ZonedDateTime endtime, Throwable throwable)
    {
        this.endtime = endtime;
        this.throwable = throwable;
    }
}
