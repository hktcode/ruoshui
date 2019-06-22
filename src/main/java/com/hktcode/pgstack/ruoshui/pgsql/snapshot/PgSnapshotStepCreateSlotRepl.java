/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 创建持久复制槽.
 *
 * @param <M> 关系的metric类.
 */
public class PgSnapshotStepCreateSlotRepl<M> implements PgSnapshotStepProcess<M>
{
    public static<M> PgSnapshotStepCreateSlotRepl<M> of //
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
        return new PgSnapshotStepCreateSlotRepl<>(config, pgdata, pgrepl, relalist);
    }
    private static final Logger logger = LoggerFactory.getLogger(PgSnapshotStepCreateSlotRepl.class);

    private final PgSnapshotConfig config;

    private final PgConnection pgrepl;

    private final Connection pgdata;

    private final ImmutableList<PgReplRelation> relalist;

    private PgSnapshotStepCreateSlotRepl
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
        String slotnameInf = config.slotnameInf;
        StringBuilder sb = new StringBuilder("CREATE_REPLICATION_SLOT ");
        sb.append(pgrepl.escapeIdentifier(slotnameInf));
        sb.append(" LOGICAL pgoutput EXPORT_SNAPSHOT");
        logger.info("create replication slot: {}", sb);
        PgReplSlotTuple tuple = PgReplSlotTuple.of(this.pgrepl, sb.toString());
        sender.sendCreateSlot(tuple, config.logDuration, metric);
        return PgSnapshotStepVerifyRelalist.<M>of(config, pgdata, pgrepl, tuple, relalist);
    }
}
