/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalDatatypeInfMsg;
import org.postgresql.jdbc.PgConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class MainlineActionNormalTypelist extends MainlineActionNormal //
    /* */< MainlineActionNormalTypelist //
    /* */, MainlineConfigNormal //
    /* */, MainlineMetricNormalTypelist //
    /* */> //
{
    static MainlineActionNormalTypelist of(MainlineActionNormalSsFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }

        MainlineConfigNormal config = action.config;
        long finish = System.currentTimeMillis();
        MainlineMetricNormalTypelist metric = MainlineMetricNormalTypelist.of(action.metric.startMillis, finish);
        AtomicReference<SimpleStatus> status = action.status;
        TransferQueue<MainlineRecord> tqueue = action.tqueue;
        return new MainlineActionNormalTypelist(config, metric, status, tqueue);
    }

    private MainlineActionNormalTypelist //
        /* */(MainlineConfigNormal config //
        /* */, MainlineMetricNormalTypelist metric //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */) //
    {
        super(config, metric, status, tqueue);
    }

    MainlineAction next(ExecutorService exesvc, PgConnection pgdata) //
        throws SQLException, InterruptedException
    {
        try (PreparedStatement ps = this.config.queryTypelist(pgdata)) {
            MainlineRecordNormal r = null;
            this.metric.statusInfor = "query data types";
            ResultSet rs = null;
            Boolean next = null;
            MainlineDeputeExecuteQuery q = MainlineDeputeExecuteQuery.of(ps);
            Future<ResultSet> rsFuture = exesvc.submit(q);
            Future<Boolean> nextFuture = null;
            MainlineDeputeResultSetNext rsDepute = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (r != null) {
                    r = this.send(r);
                }
                else if (rs == null) {
                    rs = this.pollFromFuture(rsFuture);
                } else if (nextFuture == null) {
                    metric.statusInfor = "resultSet";
                    rsDepute = MainlineDeputeResultSetNext.of(rs);
                    rs.setFetchDirection(ResultSet.FETCH_FORWARD);
                    rs.setFetchSize(this.config.rsFetchsize);
                    nextFuture = exesvc.submit(rsDepute);
                } else if (next == null) {
                    next = this.pollFromFuture(nextFuture);
                } else if (next) {
                    long d = rs.getLong("datatype");
                    String p = rs.getString("tpschema");
                    String n = rs.getString("typename");
                    LogicalDatatypeInfMsg m = LogicalDatatypeInfMsg.of(d, p, n);
                    r = MainlineRecordNormal.of(0L, m);
                    rs.setFetchSize(this.config.rsFetchsize);
                    nextFuture = exesvc.submit(rsDepute);
                    next = null;
                } else {
                    pgdata.commit();
                    metric.statusInfor = "send txation finish record, txaction starts.";
                    return MainlineActionNormalTxaction.of(this);
                }
            }
        }
        pgdata.commit();
        return MainlineActionFinish.of();
    }
}

