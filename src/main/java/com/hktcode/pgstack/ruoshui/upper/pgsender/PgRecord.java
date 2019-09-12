/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.*;

public interface PgRecord
{
    default UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshotUntilPoint sender)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        throw new NeverHappenAssertionError();
    }

    default UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshotSimpleData sender)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        throw new NeverHappenAssertionError();
    }

    default UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderMainline sender)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        throw new NeverHappenAssertionError();
    }
}
