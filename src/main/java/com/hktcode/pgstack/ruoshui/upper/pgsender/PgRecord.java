/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmActionRun;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmSenderMainline;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmSenderSnapshot;

public interface PgRecord
{
    default UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshot thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        throw new NeverHappenAssertionError();
    }

    default UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderMainline thread)
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
