/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class MainlineActionDataBegin1stSnapshot extends MainlineActionDataBegin1st //
    /* */< MainlineConfigSnapshot
    /* */>
{
    public static MainlineActionDataBegin1stSnapshot of //
        /* */( MainlineConfigSnapshot config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        return new MainlineActionDataBegin1stSnapshot(config, status, tqueue);
    }

    private MainlineActionDataBegin1stSnapshot //
        /* */( MainlineConfigSnapshot config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue, System.currentTimeMillis());
    }

    @Override
    MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException
    {
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        if (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            return MainlineActionDataRelaList.of(this);
        }
        return MainlineActionTerminateEnd.of(this);
    }
}
