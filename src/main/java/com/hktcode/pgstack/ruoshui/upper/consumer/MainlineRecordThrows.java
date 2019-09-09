/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;

public class MainlineRecordThrows implements MainlineRecord
{
    public static MainlineRecordThrows of()
    {
        return new MainlineRecordThrows();
    }

    @Override
    public UpperRecordConsumer toUpcsmRecord()
    {
        throw new FetchThreadThrowsErrorException();
    }

    private MainlineRecordThrows()
    {
    }
}
