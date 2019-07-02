/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 创建临时复制槽.
 *
 * @param <M> 关系的metric对象.
 */
public class PgSnapshotStepCreateSlotTemp<M> implements PgSnapshotStepProcess<M>
{
    public static<M> PgSnapshotStepCreateSlotTemp<M> of //
        /* */( PgSnapshotConfig config //
        /* */, Connection pgdata //
        /* */, PgConnection pgrepl //
        /* */, ImmutableList<PgReplRelation> relalist //
        /* */) //
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        if (relalist == null) {
            throw new ArgumentNullException("relalist");
        }
        return new PgSnapshotStepCreateSlotTemp<>(config, pgdata, pgrepl, relalist);
    }
    private static final Logger logger = LoggerFactory.getLogger(PgSnapshotStepCreateSlotTemp.class);

    private final PgSnapshotConfig config;

    private final PgConnection pgrepl;

    private final Connection pgdata;

    private final ImmutableList<PgReplRelation> relalist;

    private PgSnapshotStepCreateSlotTemp
        /* */( PgSnapshotConfig config
        /* */, Connection pgdata
        /* */, PgConnection pgrepl
        /* */, ImmutableList<PgReplRelation> relalist
        /* */)
    {
        this.config = config;
        this.pgdata = pgdata;
        this.pgrepl = pgrepl;
        this.relalist = relalist;
    }

    @Override
    public PgSnapshotStep<M> next(M metric, PgSnapshotSender<M> sender) //
        throws SQLException, InterruptedException
    {
        PgReplSlotTuple tuple;
        final long firstCount = (long)(Integer.MAX_VALUE * Math.random());
        long tryCount = firstCount;
        StringBuilder sb = this.buildCreateSlot(tryCount++);
        logger.info("create replication slot: {}", sb);
        while ((tuple = this.createSlot(sb.toString())) == null) {
            sb = buildCreateSlot(tryCount++);
            logger.info("duplicate slot name, change slot name and retry: tryCount={}, sql={}", tryCount - firstCount, sb);
        }
        sender.sendCreateSlot(tuple, config.logDuration, metric);
        return PgSnapshotStepVerifyRelalist.of(config, pgdata, pgrepl, tuple, relalist);
    }

    private StringBuilder buildCreateSlot(long tryCount) throws SQLException
    {
        String slotnameInf = config.slotnameInf;
        StringBuilder sb = new StringBuilder("CREATE_REPLICATION_SLOT ");
        String slotName = String.format("%s_%016x", slotnameInf, tryCount);
        sb.append(pgrepl.escapeIdentifier(slotName));
        sb.append(" TEMPORARY LOGICAL pgoutput EXPORT_SNAPSHOT");
        return sb;
    }

    private PgReplSlotTuple createSlot(String sql) throws SQLException
    {
        try {
            return PgReplSlotTuple.of(this.pgrepl, sql);
        }
        catch (PSQLException ex) {
            String sqlState = ex.getSQLState();
            if ("42710".equals(sqlState)) {
                // 复制槽已经存在，重试.
                return null;
            }
            throw ex;
        }
    }
}
