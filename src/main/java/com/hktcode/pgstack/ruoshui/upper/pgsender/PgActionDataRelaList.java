/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PgActionDataRelaList extends PgActionDataQueryRel
{
    public static PgActionDataRelaList of //
        /* */(PgConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
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
        return new PgActionDataRelaList(config, status, tqueue);
    }

    static PgActionDataRelaList of(PgActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataRelaList(action);
    }

    static PgActionDataRelaList of(PgActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataRelaList(action);
    }

    private static final Logger logger = LoggerFactory.getLogger(PgActionDataRelaList.class);

    private PgActionDataRelaList
        /* */( PgConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue);
        this.retryReason = ImmutableList.of();
        this.logDatetime = super.actionStart;
    }

    private PgActionDataRelaList(PgActionDataRelaLock action)
    {
        super(action, System.currentTimeMillis());
        this.retryReason = ImmutableList.<String>builder() //
            .addAll(action.relalist.retryReason).add("RELATION_LOCK_FAIL") //
            .build();
        this.logDatetime = action.logDatetime;
    }

    private PgActionDataRelaList(PgActionDataSizeDiff action)
    {
        super(action, System.currentTimeMillis());
        this.retryReason = ImmutableList.<String>builder() //
            .addAll(action.relalist.retryReason).add("RELALIST_SIZE_DIFF") //
            .build();
        this.logDatetime = action.logDatetime;
    }

    final ImmutableList<String> retryReason;

    @Override
    PgAction complete(Connection pgdata) throws SQLException
    {
        if (super.relaBuilder[0] != null) {
            this.newRelalist.add(super.relaBuilder[0].builder());
        }
        pgdata.commit();
        this.statusInfor = "completes";
        return PgActionDataRelaLock.of(this);
    }

    @Override
    PgDeputeStatement createDepute(PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException
    {
        return PgDeputeSelectData.of(this.config.queryRelalist(pgdata));
    }

    @Override
    public PgMetricRunRelaList toRunMetrics()
    {
        return PgMetricRunRelaList.of(this);
    }
}
