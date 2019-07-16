/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.*;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * 查询关系快照的结果.
 */
public interface PgsqlSnapshotMetricSelectTupleval extends PgsqlSnapshotMetricProcess
{
    // public static PgsqlSnapshotMetricSelectTupleval of //
    //     /* */( PgsqlSnapshotMetricVerifyRelalist metric
    //     /* */, long newDuration
    //     /* */)
    // {
    //     return new PgsqlSnapshotMetricSelectTupleval(metric, newDuration);
    // }

    // private final long newDuration;

    // private PgsqlSnapshotMetricSelectTupleval
    //     /* */( PgsqlSnapshotMetricVerifyRelalist metric
    //     /* */, long newDuration
    //     /* */)
    // {
    //     super(metric);
    //     this.newDuration = newDuration ;
    // }

    static <W extends SimpleWorker<W, PgsqlSnapshotMetric> & PgsqlSnapshot<W>>
    PgsqlSnapshotMetric next
        /* */( ExecutorService exesvc
        /* */, W worker
        /* */, PgConnection pgdata
        /* */, PgConnection pgrepl
        /* */, PgReplSlotTuple sltTupleval
        /* */, PgsqlSnapshotMetricSelectTupleval metric
        /* */, ImmutableList<PgsqlRelationMetric> relalist
        /* */)
        throws Exception
    {
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        List<PgReplRelation> relationList = new ArrayList<>(relalist.size());
        for (PgsqlRelationMetric r : relalist) {
            relationList.add(r.relationInfo);
        }
        ImmutableList<PgReplRelation> list = ImmutableList.copyOf(relationList);
        LogicalBegSnapshotMsg begSnapshotMsg = LogicalBegSnapshotMsg.of(list);
        worker.sendLogicalMsg(sltTupleval.consistentPoint, begSnapshotMsg, metric);
        try (Statement statement = pgdata.createStatement()) {
            Iterator<PgsqlRelationMetric> iter = relalist.iterator();
            Future<Long> future = null;
            long starts = System.currentTimeMillis();
            PgsqlRelationMetric relation = null;
            Long tuplevalSize = null;
            while (worker.newStatus(worker, metric) instanceof SimpleStatusInnerRun) {
                if (tuplevalSize != null) {
                    relation.tuplevalSize = tuplevalSize;
                    long finish = System.currentTimeMillis();
                    relation.selectMillis = finish - starts;
                    future = null;
                    relation = null;
                    tuplevalSize = null;
                }
                else if (future != null) {
                    tuplevalSize = worker.pollFromFuture(future);
                }
                else if (iter.hasNext()) {
                    relation = iter.next();
                    PgReplRelation r = relation.relationInfo;
                    starts = System.currentTimeMillis();
                    future = exesvc.submit(()->selectTuplevals(statement, r, worker, pgdata, sltTupleval, metric));
                }
                else {
                    LogicalEndSnapshotMsg endSnapshotMsg = LogicalEndSnapshotMsg.of(list);
                    worker.sendLogicalMsg(sltTupleval.consistentPoint, endSnapshotMsg, metric);
                    return metric.newCommit();
                }
            }
        }
        pgdata.cancelQuery();
        return metric.newCommit();
    }

    PgsqlSnapshotMetricCommit newCommit();

    static final Logger logger = LoggerFactory.getLogger(PgsqlSnapshotMetricSelectTupleval.class);

    static long selectTuplevals
        /* */( Statement s
        /* */, PgReplRelation relation
        /* */, PgsqlSnapshot worker
        /* */, PgConnection pgdata
        /* */, PgReplSlotTuple sltTupleval
        /* */, PgsqlSnapshotMetricSelectTupleval metric
        /* */) //
        throws Exception
    {
        long lsn = sltTupleval.consistentPoint;
        LogicalBegRelationMsg begRelationMsg = LogicalBegRelationMsg.of(relation);
        worker.sendLogicalMsg(lsn, begRelationMsg, metric);
        String select = worker.getTupleValSql(relation);
        if (select == null) {
            select = buildSelect(pgdata, relation);
        }
        logger.info("select relation: select={}", select);
        final JsonNode oldvalue = MissingNode.getInstance();
        long tuplevalSize = 0;
        try (ResultSet rs = s.executeQuery(select)) {
            while (rs.next()) {
                ++tuplevalSize;
                List<PgReplComponent> tupleval = new ArrayList<>();
                for (PgReplAttribute attr : relation.attrlist) {
                    String v = rs.getString(attr.attrname);
                    JsonNode newvalue = (v == null ? NullNode.getInstance() : TextNode.valueOf(v));
                    tupleval.add(PgReplComponent.of(attr, oldvalue, newvalue));
                }
                LogicalCreateTupleMsg msg = LogicalCreateTupleMsg.of(relation, ImmutableList.copyOf(tupleval));
                worker.sendLogicalMsg(lsn, msg, metric);
            }
        }
        LogicalEndRelationMsg endRelationMsg = LogicalEndRelationMsg.of(relation);
        worker.sendLogicalMsg(lsn,endRelationMsg, metric);
        return tuplevalSize;
    }

    static String buildSelect(PgConnection c, PgReplRelation r) //
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
