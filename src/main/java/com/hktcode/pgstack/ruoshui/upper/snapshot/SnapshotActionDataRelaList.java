/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineDeputeExecuteQuery;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineDeputeResultSetNext;
import com.hktcode.pgstack.ruoshui.upper.mainline.RelationBuilder;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class SnapshotActionDataRelaList extends SnapshotActionData
{
    private static final Logger logger = LoggerFactory.getLogger(SnapshotActionDataRelaList.class);

    public static SnapshotActionDataRelaList of //
        /* */( SnapshotConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<UpcsmFetchRecordSnapshot> tqueue //
        /* */, long actionStart //
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
        return new SnapshotActionDataRelaList(config, status, tqueue, actionStart);
    }

    public static SnapshotActionDataRelaList of(SnapshotActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataRelaList(action);
    }
    public static SnapshotActionDataRelaList of(SnapshotActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionDataRelaList(action);
    }

    private SnapshotActionDataRelaList
        /* */( SnapshotConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<UpcsmFetchRecordSnapshot> tqueue //
        /* */, long actionStart //
        /* */)
    {
        super(config, status, tqueue, actionStart);
        this.retryReason = ImmutableList.of();
    }

    private SnapshotActionDataRelaList(SnapshotActionDataRelaLock action)
    {
        super(action, System.currentTimeMillis());
        this.retryReason = ImmutableList.<String>builder()
            .addAll(action.relalist.retryReason)
            .add("TABLE_LOCK_FAIL").build();
    }

    private SnapshotActionDataRelaList(SnapshotActionDataSizeDiff action)
    {
        super(action, System.currentTimeMillis());
        this.retryReason = ImmutableList.<String>builder()
            .addAll(action.relalist.retryReason)
            .add("TABLE_SIZE_DIFF").build();
    }

    final ImmutableList<String> retryReason;

    public final List<PgsqlRelationMetric> relationLst = new ArrayList<>();

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
        try (PreparedStatement ps = this.config.queryRelalist(pgdata)) {
            ResultSet rs = null;
            Boolean next = null;
            // TODO: 其实不需要这个统计.
            long nextstarts = System.currentTimeMillis();
            MainlineDeputeExecuteQuery q = MainlineDeputeExecuteQuery.of(ps);
            this.statusInfor = "doingtask: executeQuery";
            Future<ResultSet> rsFuture = exesvc.submit(q);
            Future<Boolean> nextFuture = null;
            MainlineDeputeResultSetNext rsDepute = null;
            RelationBuilder[] builder = new RelationBuilder[] { null };
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (rs == null) {
                    rs = this.pollFromFuture(rsFuture);
                } else if (nextFuture == null) {
                    long finish = System.currentTimeMillis();
                    long duration = finish - nextstarts;
                    logger.info("executeQuery: duration={}", duration);
                    nextstarts = System.currentTimeMillis();
                    this.statusInfor = "doingtask: resultSet";
                    rsDepute = MainlineDeputeResultSetNext.of(rs);
                    rs.setFetchDirection(ResultSet.FETCH_FORWARD);
                    rs.setFetchSize(this.config.rsFetchsize);
                    nextFuture = exesvc.submit(rsDepute);
                } else if (next == null) {
                    next = this.pollFromFuture(nextFuture);
                } else if (next) {
                    this.build(rs, builder);
                    nextstarts = System.currentTimeMillis();
                    rs.setFetchSize(this.config.rsFetchsize);
                    nextFuture = exesvc.submit(rsDepute);
                    next = null;
                } else {
                    this.build(builder);
                    pgdata.commit();
                    this.statusInfor = "completes";
                    return SnapshotActionDataRelaLock.of(this);
                }
            }
            pgdata.commit();
        }
        catch (Exception ex) {
            if (!pgdata.isClosed()) {
                pgdata.rollback();
            }
            this.statusInfor = "exception: message=" + ex.getMessage();
            throw ex;
        }
        this.statusInfor = "terminate";
        return SnapshotActionTerminateEnd.of(this);
    }

    private void build(RelationBuilder[] builder)
    {
        if (builder[0] != null) {
            this.relationLst.add(builder[0].builder());
        }
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
        } else if (builder[0].metadata.relident != relident) {
            PgsqlRelationMetric r = builder[0].builder();
            if (this.config.whereRelalist(r.relationInfo)) {
                this.relationLst.add(r);
            }
            String dbschema = rs.getString("dbschema");
            String relation = rs.getString("relation");
            long replchar = rs.getLong("replchar");
            builder[0] = RelationBuilder.of(relident, dbschema, relation, replchar);
        }
        PgReplAttribute attr = PgReplAttribute.of //
            /* */( attrname //
            /* */, tpschema //
            /* */, typename //
            /* */, -1 //
            /* */, attflags //
            /* */, datatype //
            /* */, attypmod //
            /* */);
        builder[0].attrlist.add(attr);
    }

    @Override
    public SnapshotMetricEnd toEndMetrics()
    {
        return SnapshotMetricEndRelaList.of(this);
    }

    @Override
    public SnapshotMetricRun toRunMetrics()
    {
        return SnapshotMetricRunRelaList.of(this);
    }
}
