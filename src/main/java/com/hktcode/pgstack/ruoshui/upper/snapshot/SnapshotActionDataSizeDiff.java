/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.mainline.*;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class SnapshotActionDataSizeDiff extends SnapshotActionData
{
    static SnapshotActionDataSizeDiff of(SnapshotActionDataReplSlot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataSizeDiff(action);
    }

    private static final Logger logger = LoggerFactory.getLogger(SnapshotActionDataSizeDiff.class);

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relaLock;

    public final SnapshotReportReplSlotTuple replSlot;

    final ImmutableList<PgsqlRelationMetric> oldRelalist;

    private final List<PgsqlRelationMetric> newRelalist;

    private SnapshotActionDataSizeDiff(SnapshotActionDataReplSlot action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = SnapshotReportReplSlotTuple.of(action, this.actionStart);
        this.oldRelalist = action.relationLst;
        this.newRelalist = new ArrayList<>();
        this.logDatetime = action.logDatetime;
    }

    @Override
    SnapshotAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException, ScriptException, InterruptedException
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
        String name = this.replSlot.createTuple.snapshotName;
        String setTransaction //
            = "SET TRANSACTION SNAPSHOT '" + pgdata.escapeLiteral(name) + "'";
        try (Statement s = pgdata.createStatement()) {
            logger.info("execute set snapshot: {}", setTransaction);
            s.execute(setTransaction); // TODO: pollFromFuture?
        }
        try (PreparedStatement ps = this.config.queryRelalist(pgdata)) {
            ResultSet rs = null;
            Boolean next = null;
            Future<ResultSet> rsFuture = exesvc.submit(MainlineDeputeExecuteQuery.of(ps));
            Future<Boolean> nextFuture = null;
            MainlineDeputeResultSetNext rsDepute = null;
            RelationBuilder[] builder = new RelationBuilder[] { null };
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (rs == null) {
                    rs = this.pollFromFuture(rsFuture);
                }
                else if (nextFuture == null) {
                    rsDepute = MainlineDeputeResultSetNext.of(rs);
                    nextFuture = exesvc.submit(rsDepute);
                }
                else if (next == null) {
                    next = this.pollFromFuture(nextFuture);
                }
                else if (next) {
                    this.build(rs, builder);
                    nextFuture = exesvc.submit(rsDepute);
                    next = null;
                }
                else if (this.build(builder).size() == this.oldRelalist.size()) {
                    return SnapshotActionDataSsBegins.of(this);
                }
                else {
                    return SnapshotActionDataRelaList.of(this);
                }
            }
        }
        return SnapshotActionTerminateEnd.of(this);
    }

    private List<PgsqlRelationMetric> build(RelationBuilder[] builder)
    {
        if (builder[0] != null) {
            this.newRelalist.add(builder[0].builder());
        }
        return this.newRelalist;
    }

    private void build(ResultSet rs, RelationBuilder[] builder)
        throws SQLException, ScriptException
    {
        long relident = rs.getLong("relident");
        long attflags = rs.getLong("attflags");
        String attrname = rs.getString("attrname");
        long datatype = rs.getLong("datatype");
        long attypmod = rs.getLong("attypmod");
        String tpschema = rs.getString("tpschema");
        String typename = rs.getString("typename");
        if (builder[0] == null) {
            String dbschema = rs.getString("dbschema");
            String relation = rs.getString("relation");
            long replchar = rs.getLong("replchar");
            builder[0] = RelationBuilder.of(relident, dbschema, relation, replchar);
        }
        else if (builder[0].metadata.relident != relident) {
            PgsqlRelationMetric r = builder[0].builder();
            if (this.config.whereRelalist(r.relationInfo)) {
                this.newRelalist.add(r);
            }
            String dbschema = rs.getString("dbschema");
            String relation = rs.getString("relation");
            long replchar = rs.getLong("replchar");
            builder[0] = RelationBuilder.of(relident, dbschema, relation, replchar);
        }
        PgReplAttribute attr = PgReplAttribute.of(attrname, tpschema, typename, -1, attflags, datatype, attypmod);
        builder[0].attrlist.add(attr);
    }

    @Override
    public SnapshotMetricRunSizeDiff toRunMetrics()
    {
        return SnapshotMetricRunSizeDiff.of(this);
    }

    @Override
    public SnapshotMetricEndSizeDiff toEndMetrics()
    {
        return SnapshotMetricEndSizeDiff.of(this);
    }
}
