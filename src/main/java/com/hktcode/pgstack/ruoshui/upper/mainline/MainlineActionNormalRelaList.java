/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class MainlineActionNormalRelaList extends MainlineActionNormal //
    /* */< MainlineActionNormalRelaList //
    /* */, MainlineConfigNormal //
    /* */, MainlineMetricNormalRelaList //
    /* */> //
{
    public static MainlineActionNormalRelaList of //
        /* */(MainlineConfigNormal config //
        /* */, MainlineMetricNormalRelaList metric //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        return new MainlineActionNormalRelaList(config, metric, status, tqueue);
    }

    private static final Logger logger = LoggerFactory.getLogger(MainlineActionNormalRelaList.class);

    private MainlineActionNormalRelaList //
        /* */(MainlineConfigNormal config //
        /* */, MainlineMetricNormalRelaList metric //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        super(config, metric, status, tqueue);
    }

    MainlineAction next(ExecutorService exesvc, PgConnection pgdata) //
        throws Exception
    {
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        try (PreparedStatement ps = this.config.queryRelalist(pgdata)) {
            ResultSet rs = null;
            Boolean next = null;
            // TODO: 其实不需要这个统计.
            long nextstarts = System.currentTimeMillis();
            MainlineDeputeExecuteQuery q = MainlineDeputeExecuteQuery.of(ps);
            metric.statusInfor = "executeQuery";
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
                    this.metric.maxnextTime = duration;
                    nextstarts = System.currentTimeMillis();
                    metric.statusInfor = "resultSet";
                    rsDepute = MainlineDeputeResultSetNext.of(rs);
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
                    metric.statusInfor = "complete";
                    return MainlineActionNormalRelaLock.of(this);
                }
            }
            pgdata.commit();
        }
        catch (Exception ex) {
            if (!pgdata.isClosed()) {
                pgdata.rollback();
            }
            metric.statusInfor = "exception: message=" + ex.getMessage();
            throw ex;
        }
        metric.statusInfor = "delete";
        return MainlineActionFinish.of();
    }

    private void build(RelationBuilder[] builder, long duration)
    {
        if (this.metric.maxnextTime < duration) {
            logger.info("get max next time: new={}", duration);
            this.metric.maxnextTime = duration;
        }
        this.metric.relationLst.add(builder[0].builder());
    }

    private void build(ResultSet rs, RelationBuilder[] builder, long duration)
        throws SQLException, ScriptException
    {
        if (this.metric.maxnextTime < duration) {
            logger.info("get max next time: new={}", duration);
            this.metric.maxnextTime = duration;
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
                this.metric.relationLst.add(r);
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
}
