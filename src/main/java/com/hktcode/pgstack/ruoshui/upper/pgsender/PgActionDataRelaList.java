/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;
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

public class PgActionDataRelaList extends PgActionData
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
        /* */(PgConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue, System.currentTimeMillis());
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

    @Deprecated
    public long getrsMillis = -1;

    @Deprecated
    public long maxnextTime = -1;

    final ImmutableList<String> retryReason;

    public final List<PgsqlRelationMetric> relationLst = new ArrayList<>();

    @Override
    public PgAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
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
            DeputeExecuteQueryMainline q = DeputeExecuteQueryMainline.of(ps);
            this.statusInfor = "doingtask: executeQuery";
            Future<ResultSet> rsFuture = exesvc.submit(q);
            Future<Boolean> nextFuture = null;
            DeputeResultSetNextMainline rsDepute = null;
            RelationBuilder[] builder = new RelationBuilder[] { null };
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (rs == null) {
                    rs = this.pollFromFuture(rsFuture);
                } else if (nextFuture == null) {
                    long finish = System.currentTimeMillis();
                    long duration = finish - nextstarts;
                    logger.info("executeQuery: duration={}", duration);
                    this.maxnextTime = duration;
                    nextstarts = System.currentTimeMillis();
                    this.statusInfor = "doingtask: resultSet";
                    rsDepute = DeputeResultSetNextMainline.of(rs);
                    rs.setFetchDirection(ResultSet.FETCH_FORWARD);
                    rs.setFetchSize(this.config.rsFetchsize);
                    nextFuture = exesvc.submit(rsDepute);
                } else if (next == null) {
                    next = this.pollFromFuture(nextFuture);
                } else if (next) {
                    long finish = System.currentTimeMillis();
                    long duration = (finish - nextstarts);
                    this.build(rs, builder, duration);
                    nextstarts = System.currentTimeMillis();
                    rs.setFetchSize(this.config.rsFetchsize);
                    nextFuture = exesvc.submit(rsDepute);
                    next = null;
                } else {
                    long finish = System.currentTimeMillis();
                    long duration = finish - nextstarts;
                    this.build(builder, duration);
                    pgdata.commit();
                    this.statusInfor = "completes";
                    return PgActionDataRelaLock.of(this);
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
        return PgActionTerminateEnd.of(this);
    }

    private void build(RelationBuilder[] builder, long duration)
    {
        if (this.maxnextTime < duration) {
            logger.info("get max next time: new={}, old={}", duration, this.maxnextTime);
            this.maxnextTime = duration;
        }
        if (builder[0] != null) {
            this.relationLst.add(builder[0].builder());
        }
    }

    private void build(ResultSet rs, RelationBuilder[] builder, long duration)
        throws SQLException, ScriptException
    {
        if (this.maxnextTime < duration) {
            logger.info("get max next time: new={}, old={}", duration, this.maxnextTime);
            this.maxnextTime = duration;
        }
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
    public PgMetricRunRelaList toRunMetrics()
    {
        return PgMetricRunRelaList.of(this);
    }

    @Override
    public PgMetricEndRelaList toEndMetrics()
    {
        return PgMetricEndRelaList.of(this);
    }
}