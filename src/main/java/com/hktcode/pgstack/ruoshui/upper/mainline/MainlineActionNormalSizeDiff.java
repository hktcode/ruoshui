/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
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
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionNormalSizeDiff extends MainlineActionNormal //
    /* */< MainlineActionNormalSizeDiff //
    /* */, MainlineConfigNormal //
    /* */, MainlineMetricNormalSizeDiff //
    /* */> //
{
    public static MainlineActionNormalSizeDiff of (MainlineActionNormalReplSlot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        long finish = System.currentTimeMillis();
        long totalPeriod = finish - action.metric.actionStart;
        MainlineReportReplSlot replSlot = MainlineReportReplSlot.of
            (totalPeriod, action.metric.sltDuration, action.metric.createTuple[0]);
        MainlineMetricNormalSizeDiff metric = MainlineMetricNormalSizeDiff.of
            /* */( action.metric.startMillis
            /* */, action.metric.relalist
            /* */, action.metric.relaLock
            /* */, replSlot
            /* */, action.metric.relationLst
            /* */, finish
            /* */);
        metric.fetchCounts += action.metric.fetchCounts;
        metric.recordCount += action.metric.recordCount;
        metric.fetchCounts += action.metric.fetchCounts;
        metric.fetchMillis += action.metric.fetchMillis;
        metric.offerCounts += action.metric.offerCounts;
        metric.offerMillis += action.metric.offerMillis;
        metric.logDatetime = action.metric.logDatetime;
        return new MainlineActionNormalSizeDiff
            /* */( action.config
            /* */, metric
            /* */, action.tqueue
            /* */, action.status
            /* */);
    }

    private static final Logger logger = LoggerFactory.getLogger(MainlineActionNormalSizeDiff.class);

    MainlineActionNormalSizeDiff
        /* */( MainlineConfigNormal config
        /* */, MainlineMetricNormalSizeDiff metric
        /* */, TransferQueue<MainlineRecord> tqueue
        /* */, AtomicReference<SimpleStatus> status
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
        String name = this.metric.replSlot.createTuple.snapshotName;
        String setTransaction //
            = "SET TRANSACTION SNAPSHOT '" + pgdata.escapeLiteral(name) + "'";
        try (Statement s = pgdata.createStatement()) {
            logger.info("execute set snapshot: {}", setTransaction);
            s.execute(setTransaction);
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
                else {
                    this.build(builder);
                    if (this.metric.oldRelalist.size() == this.metric.newRelalist.size()) {
                        return MainlineActionNormalSsBegins.of(this);
                    }
                    else {
                        long finish = System.currentTimeMillis();
                        ImmutableList<String> retryReason //
                            = ImmutableList.<String>builder()
                            .addAll(this.metric.relalist.retryReason)
                            .add("relalist change").build();
                        MainlineMetricNormalRelaList m = MainlineMetricNormalRelaList.of //
                            (this.metric.startMillis, finish, retryReason);
                        return MainlineActionNormalRelaList.of //
                            (this.config, m, this.status, this.tqueue);
                    }
                }
            }
        }
        return MainlineActionFinish.of();
    }

    private void build(RelationBuilder[] builder)
    {
        this.metric.newRelalist.add(builder[0].builder());
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
                this.metric.newRelalist.add(r);
            }
            String dbschema = rs.getString("dbschema");
            String relation = rs.getString("relation");
            long replchar = rs.getLong("replchar");
            builder[0] = RelationBuilder.of(relident, dbschema, relation, replchar);
        }
        PgReplAttribute attr = PgReplAttribute.of(attrname, tpschema, typename, -1, attflags, datatype, attypmod);
        builder[0].attrlist.add(attr);
    }
}
