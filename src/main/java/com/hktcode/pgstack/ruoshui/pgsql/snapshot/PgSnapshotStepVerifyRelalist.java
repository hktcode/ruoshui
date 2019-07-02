/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 验证中间是否有新增关系.
 *
 * @param <M>
 */
public class PgSnapshotStepVerifyRelalist<M> implements PgSnapshotStepProcess<M>
{
    public static<M> PgSnapshotStepVerifyRelalist<M> of //
        /* */(PgSnapshotConfig config //
        /* */, Connection pgdata //
        /* */, PgConnection pgrepl //
        /* */, PgReplSlotTuple replSlot //
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
        if (replSlot == null) {
            throw new ArgumentNullException("replSlot");
        }
        if (relalist == null) {
            throw new ArgumentNullException("relalist");
        }
        return new PgSnapshotStepVerifyRelalist<>(config, pgdata, pgrepl, replSlot, relalist);
    }

    private final PgSnapshotConfig config;

    private final Connection pgdata;

    private final PgConnection pgrepl;

    private final PgReplSlotTuple replSlot;

    private final ImmutableList<PgReplRelation> relalist;

    private PgSnapshotStepVerifyRelalist
        /* */( PgSnapshotConfig config
        /* */, Connection pgdata
        /* */, PgConnection pgrepl
        /* */, PgReplSlotTuple replSlot
        /* */, ImmutableList<PgReplRelation> relalist
        /* */)
    {
        this.config = config;
        this.pgdata = pgdata;
        this.pgrepl = pgrepl;
        this.replSlot = replSlot;
        this.relalist = relalist;
    }

    private static final Logger logger = LoggerFactory.getLogger(PgSnapshotStepVerifyRelalist.class);

    @Override
    public PgSnapshotStep<M> next(M metric, PgSnapshotSender<M> sender) //
        throws SQLException, ScriptException
    {
        PgConnection pgcon = this.pgdata.unwrap(PgConnection.class);
        String setTransaction = "SET TRANSACTION SNAPSHOT '" //
            + pgcon.escapeLiteral(replSlot.snapshotName) //
            + "'";
        try (Statement statement = this.config.createStatement(this.pgdata)) {
            logger.info("execute set snapshot: {}", setTransaction);
            statement.execute(setTransaction);
        }

        ImmutableList<PgReplRelation> list = config.queryForRelations(pgdata);
        if (list.size() == this.relalist.size()) {
            return PgSnapshotStepSelectTupleval.of(config, pgcon, replSlot, this.relalist);
        }
        else {
            try (Statement s = pgrepl.createStatement()) {
                String slotname = pgrepl.escapeIdentifier(replSlot.slotName);
                s.execute("DROP_REPLICATION_SLOT " + slotname);
            }
            return PgSnapshotStepAbortsTxaction.of();
        }
    }
}
