/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.*;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询关系快照的结果.
 *
 * @param <M> 关系的metric对象.
 */
public class PgSnapshotStepSelectTupleval<M> implements PgSnapshotStepProcess<M>
{
    public static<M> PgSnapshotStepSelectTupleval<M> of //
        /* */( PgSnapshotConfig config
        /* */, PgConnection pgdata
        /* */, PgReplSlotTuple replSlot
        /* */, ImmutableList<PgReplRelation> relalist
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        if (replSlot == null) {
            throw new ArgumentNullException("replSlot");
        }
        if (relalist == null) {
            throw new ArgumentNullException("relalist");
        }
        return new PgSnapshotStepSelectTupleval<>(config, pgdata, replSlot, relalist);

    }
    private final PgSnapshotConfig config;

    private final PgConnection pgdata;

    private final PgReplSlotTuple replSlot;

    private final ImmutableList<PgReplRelation> relalist;

    private PgSnapshotStepSelectTupleval
        /* */( PgSnapshotConfig config
        /* */, PgConnection pgdata
        /* */, PgReplSlotTuple replSlot
        /* */, ImmutableList<PgReplRelation> relalist
        /* */)
    {
        this.config = config;
        this.pgdata = pgdata;
        this.replSlot = replSlot;
        this.relalist = relalist;
    }

    @Override
    public PgSnapshotStep<M> next(M metric, PgSnapshotSender<M> sender)
        throws SQLException, InterruptedException
    {
        long lsn = replSlot.consistentPoint;
        LogicalBegSnapshotMsg begSnapshotMsg = LogicalBegSnapshotMsg.of(relalist);
        sender.sendLogicalMsg(lsn, begSnapshotMsg, config.logDuration, metric);
        try (Statement statement = this.config.createStatement(pgdata)) {
            for (PgReplRelation relation : relalist) {
                this.selectTuplevals(statement, relation, lsn, metric, sender);
            }
        }
        LogicalEndSnapshotMsg endSnapshotMsg = LogicalEndSnapshotMsg.of(relalist);
        sender.sendLogicalMsg(lsn, endSnapshotMsg, config.logDuration, metric);
        return PgSnapshotStepCommitTxaction.<M>of();
    }

    private static final Logger logger = LoggerFactory.getLogger(PgSnapshotStepSelectTupleval.class);

    private void selectTuplevals(Statement s, PgReplRelation relation, long lsn, M metric, PgSnapshotSender<M> sender) //
        throws SQLException, InterruptedException
    {
        PgReplRelationName name = PgReplRelationName.of(relation.dbschema, relation.relation);
        LogicalBegRelationMsg begRelationMsg = LogicalBegRelationMsg.of(relation);
        sender.sendLogicalMsg(lsn, begRelationMsg, config.logDuration, metric);
        String select = this.config.tupleSelect.get(name);
        if (select == null) {
            select = buildSelect(this.pgdata, relation);
        }
        logger.info("select relation: select={}", select);
        final JsonNode oldvalue = MissingNode.getInstance();
        try (ResultSet rs = s.executeQuery(select)) {
            while (rs.next()) {
                List<PgReplComponent> tupleval = new ArrayList<>();
                for (PgReplAttribute attr : relation.attrlist) {
                    String v = rs.getString(attr.attrname);
                    JsonNode newvalue = (v == null ? NullNode.getInstance() : TextNode.valueOf(v));
                    tupleval.add(PgReplComponent.of(attr, oldvalue, newvalue));
                }
                LogicalCreateTupleMsg msg = LogicalCreateTupleMsg.of(relation, ImmutableList.copyOf(tupleval));
                sender.sendLogicalMsg(lsn, msg, config.logDuration, metric);
            }
        }
        LogicalEndRelationMsg endRelationMsg = LogicalEndRelationMsg.of(relation);
        sender.sendLogicalMsg(lsn, endRelationMsg, config.logDuration, metric);
    }

    private static String buildSelect(PgConnection c, PgReplRelation r) //
        throws SQLException
    {
        StringBuilder sb = new StringBuilder("\nSELECT ");
        String n = c.escapeIdentifier(r.attrlist.get(0).attrname);
        sb.append(n);
        sb.append("::text as ");
        sb.append(n);
        for(int i = 1; i < r.attrlist.size(); ++i) {
            sb.append("\n     , ");
            n = c.escapeIdentifier(r.attrlist.get(i).attrname);
            sb.append(n);
            sb.append("::text as ");
            sb.append(n);
        }
        sb.append("\nFROM ");
        sb.append(c.escapeIdentifier(r.dbschema));
        sb.append(".");
        sb.append(c.escapeIdentifier(r.relation));
        return sb.toString();
    }
}
