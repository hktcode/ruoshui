/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;

public interface MainlineRecord
{
    UpperRecordConsumer toUpcsmRecord();
}
