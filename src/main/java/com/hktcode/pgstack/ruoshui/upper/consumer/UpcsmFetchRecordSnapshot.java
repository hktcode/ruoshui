/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;

public interface UpcsmFetchRecordSnapshot extends UpcsmFetchRecord
{
    default UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmThreadSnapshot thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        throw new NeverHappenAssertionError();
    }

    default UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmThreadMainline thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        throw new NeverHappenAssertionError();
    }
}
