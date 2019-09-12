/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

abstract class PgActionDataQuerySql extends PgActionData
{
    private static final Logger logger = LoggerFactory.getLogger(PgActionDataQuerySql.class);

    @Deprecated
    private long maxnextTime = -1;

    PgActionDataQuerySql
        /* */( PgConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue, System.currentTimeMillis());
    }

    PgActionDataQuerySql(PgActionData action, long actionStart) //
    {
        super(action, actionStart);
    }

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
        try (PgDeputeStatement ps = this.createDepute(pgdata, pgrepl)) {
            PgRecord record = this.initRecord();
            ResultSet rs = null;
            Boolean next = null;
            // TODO: 其实不需要这个统计.
            long nextstarts = System.currentTimeMillis();
            this.statusInfor = "doingtask: executeQuery";
            Future<ResultSet> rsFuture = exesvc.submit(ps);
            Future<Boolean> nextFuture = null;
            DeputeResultSetNextMainline rsDepute = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (record != null) {
                    record = this.send(record);
                } else if (rs == null) {
                    rs = this.pollFromFuture(rsFuture);
                } else if (nextFuture == null) {
                    long finish = System.currentTimeMillis();
                    long duration = finish - nextstarts;
                    logger.info("executeQuery: duration={}", duration);
                    this.maxnextTime = duration;
                    nextstarts = System.currentTimeMillis();
                    this.statusInfor = "doingtask: resultSet";
                    rs.setFetchDirection(ResultSet.FETCH_FORWARD);
                    rs.setFetchSize(this.config.rsFetchsize);
                    rsDepute = DeputeResultSetNextMainline.of(rs);
                    nextFuture = exesvc.submit(rsDepute);
                } else if (next == null) {
                    next = this.pollFromFuture(nextFuture);
                } else if (next) {
                    this.updateMaxnextTime(nextstarts);
                    record = this.build(rs);
                    nextstarts = System.currentTimeMillis();
                    rs.setFetchSize(this.config.rsFetchsize);
                    nextFuture = exesvc.submit(rsDepute);
                    next = null;
                } else {
                    this.updateMaxnextTime(nextstarts);
                    return this.complete(pgdata);
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

    private void updateMaxnextTime(long starts)
    {
        long finish = System.currentTimeMillis();
        long duration = finish - starts;
        long currmaxTime = this.maxnextTime;
        if (currmaxTime >= duration) {
            return;
        }
        logger.info("rs.next max take: new={}, old={}", duration, currmaxTime);
        this.maxnextTime = duration;
    }

    PgRecord initRecord()
    {
        return null;
    }

    abstract PgRecord build(ResultSet rs) throws SQLException, ScriptException;

    abstract PgAction complete(Connection pgdata) throws SQLException;

    abstract PgDeputeStatement createDepute(PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException;
}
