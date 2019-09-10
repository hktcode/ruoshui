/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmActionRun;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmThreadSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgRecordExecThrows implements UpcsmFetchRecordSnapshot
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
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmThreadSnapshot thread)
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

    private PgRecordExecThrows(Throwable throwable)
    {
        this.throwable = throwable;
    }
}
