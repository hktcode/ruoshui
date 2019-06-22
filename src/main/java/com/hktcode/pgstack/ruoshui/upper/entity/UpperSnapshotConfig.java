/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalStreamStarter;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThreadBasic;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.snapshot.init.UpperSnapshotInitThread;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.atomic.AtomicReference;

public class UpperSnapshotConfig extends UpperConsumerConfig
{
    public static UpperSnapshotConfig of //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalStreamStarter logicalSlot //
        /* */, PgSnapshotConfig iniSnapshot //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */)
    {
        if (srcProperty == null) {
            throw new ArgumentNullException("srcProperty");
        }
        if (logicalSlot == null) {
            throw new ArgumentNullException("logicalSlot");
        }
        if (iniSnapshot == null) {
            throw new ArgumentNullException("iniSnapshot");
        }
        return new UpperSnapshotConfig //
            /* */( srcProperty //
            /* */, logicalSlot //
            /* */, iniSnapshot //
            /* */, waitTimeout //
            /* */, logDuration //
            /* */);
    }

    public final PgSnapshotConfig iniSnapshot;

    private UpperSnapshotConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalStreamStarter logicalSlot //
        /* */, PgSnapshotConfig iniSnapshot //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */)
    {
        super(srcProperty, logicalSlot, waitTimeout, logDuration);
        this.iniSnapshot = iniSnapshot;
    }

    @Override
    public UpperConsumerThreadBasic createAction //
        /* */( PgConnection pgrepl //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */) throws InterruptedException //
    {
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return UpperSnapshotInitThread.of(this, status, pgrepl);
    }
}
