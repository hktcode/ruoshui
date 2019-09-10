/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.FetchThreadThrowsErrorException;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmActionRun;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmSenderMainline;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmSenderSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgRecordExecThrows implements PgRecord
{
    public static PgRecordExecThrows of(Throwable throwable)
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        return new PgRecordExecThrows(throwable);
    }

    public final Throwable throwable;

    private static final Logger logger = LoggerFactory.getLogger(PgRecordExecThrows.class);

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshot thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("fetch");
        }
        logger.error("throws exception: ", throwable);
        action.fetchThread = thread.mlxact;
        return null;
    }

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderMainline thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        throw new FetchThreadThrowsErrorException();
    }

    private PgRecordExecThrows(Throwable throwable)
    {
        this.throwable = throwable;
    }
}
