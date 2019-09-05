/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperThreadThrowsException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.consumer.FetchThreadThrowsErrorException;

import java.time.ZonedDateTime;

class MainlineRecordThrows implements MainlineRecord
{
    static MainlineRecordThrows of()
    {
        return new MainlineRecordThrows();
    }

    @Override
    public UpperConsumerRecord toUpcsmRecord()
    {
        throw new FetchThreadThrowsErrorException();
    }

    private MainlineRecordThrows()
    {
    }
}
