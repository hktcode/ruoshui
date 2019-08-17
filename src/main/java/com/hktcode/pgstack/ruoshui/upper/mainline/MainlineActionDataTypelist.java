/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalDatatypeInfMsg;
import org.postgresql.jdbc.PgConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

abstract class MainlineActionDataTypelist
    extends MainlineActionData<MainlineConfig>
{
    public final MainlineReportBegin1st begin1st;

    protected <T extends MainlineActionDataBegin1st<F>, F extends MainlineConfig>
    MainlineActionDataTypelist(T action)
    {
        super(action, System.currentTimeMillis());
        this.begin1st = MainlineReportBegin1st.of(action, this.actionStart);
        this.logDatetime = action.logDatetime;
    }

    protected <T extends MainlineActionDataSsFinish>
    MainlineActionDataTypelist(T action)
    {
        super(action, System.currentTimeMillis());
        this.begin1st = action.begin1st;
        this.logDatetime = action.logDatetime;
    }

    @SuppressWarnings("unchecked")
    @Override
    MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException, InterruptedException
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
        try (PreparedStatement ps = this.config.queryTypelist(pgdata)) {
            MainlineRecord r = null;
            this.statusInfor = "query data types";
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
                    this.statusInfor = "resultSet";
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
                    this.statusInfor = "send txation finish record, txaction starts.";
                    return this.txaction();
                }
            }
        }
        pgdata.commit();
        return MainlineActionTerminateEnd.of(this);
    }

    public abstract MainlineActionReplTxaction txaction();
}

