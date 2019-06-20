/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.core.Utils;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 对指定的关系加锁.
 * @param <M> 关系的metric类型.
 */
public class PgSnapshotStepLockedRelalist<M> implements PgSnapshotStepProcess<M>
{
    private static final Logger logger = LoggerFactory.getLogger(PgSnapshotStepLockedRelalist.class);

    public static<M> PgSnapshotStepLockedRelalist<M> of//
        /* */( PgSnapshotConfig config //
        /* */, Connection pgdata //
        /* */, PgConnection pgrepl //
        /* */, ImmutableList<PgReplRelation> relalist //
        /* */)
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
        return new PgSnapshotStepLockedRelalist<>(config, pgdata, pgrepl, relalist);
    }

    private final PgSnapshotConfig config;

    private final Connection pgdata;

    private final PgConnection pgrepl;

    private final ImmutableList<PgReplRelation> relalist;

    private PgSnapshotStepLockedRelalist //
        /* */( PgSnapshotConfig config //
        /* */, Connection pgdata //
        /* */, PgConnection pgrepl //
        /* */, ImmutableList<PgReplRelation> relalist //
        /* */) //
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
        // TODO: 研究一下PostgreSQL的锁机制，看看能不能加上更低级的锁.
        try (Statement statement = config.createStatement(pgdata)) {
            for (PgReplRelation r : relalist) {
                String d = r.dbschema;
                String l = r.relation;
                StringBuilder sbuf = new StringBuilder("LOCK TABLE ");
                Utils.escapeIdentifier(sbuf, d);
                sbuf.append('.');
                Utils.escapeIdentifier(sbuf, l);
                sbuf.append(" IN SHARE UPDATE EXCLUSIVE MODE");
                String lockTableSql = sbuf.toString();
                try {
                    statement.execute(lockTableSql);
                }
                catch (PSQLException ex) {
                    if ("42P01".equals(ex.getSQLState())) {
                        // 关系在查询和加锁期间被删除了，回滚事务并重试.
                        logger.info("relation does not exist: relation={}", r);
                        return PgSnapshotStepAbortsTxaction.<M>of();
                    }
                    throw ex;
                }
            }
            sender.sendPauseWorld(config.logDuration, metric);
            if (this.config.isTemporary) {
                return PgSnapshotStepCreateSlotTemp.<M>of(config, pgdata, pgrepl, relalist);
            }
            else {
                return PgSnapshotStepCreateSlotRepl.<M>of(config, pgdata, pgrepl, relalist);
            }
        }
    }
}
