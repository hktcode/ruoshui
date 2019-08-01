/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.concurrent.*;

public interface PgsqlSnapshot<W extends PgsqlSnapshot<W>> extends BgWorker<W>
{
    Logger logger = LoggerFactory.getLogger(PgsqlSnapshot.class);

    static <W extends PgsqlSnapshot<W>> void run(W worker) throws Exception
    {
        ZonedDateTime startMillis = ZonedDateTime.now();
        ExecutorService exesvc = Executors.newSingleThreadExecutor();
        PgConnection c = worker.dataConnection();
        boolean readonly = c.isReadOnly();
        int transactionIsolation = c.getTransactionIsolation();
        boolean autoCommit = c.getAutoCommit();
        try {
            c.setReadOnly(true);
            c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            c.setAutoCommit(false);
            long retriesCount = -1;
            PgsqlSnapshotMetric metric;
            do {
                metric = worker.newSelectRelalist(startMillis, ++retriesCount) //
                    .next(exesvc, worker, c);
                if (metric instanceof PgsqlSnapshotMetricFinish) {
                    continue;
                }
                c.commit();
                try (Connection repl = worker.replConnection()) {
                    PgConnection pgrepl = repl.unwrap(PgConnection.class);
                    do {
                        PgsqlSnapshotMetricProcess prc = (PgsqlSnapshotMetricProcess)metric;
                        metric = prc.next(exesvc, worker, c, pgrepl);
                    } while (metric instanceof PgsqlSnapshotMetricProcess);
                }
            } while (!((PgsqlSnapshotMetricFinish)metric).finish(c));
        }
        finally {
            if (!c.isClosed()) {
                c.setReadOnly(readonly);
                c.setTransactionIsolation(transactionIsolation);
                c.setAutoCommit(autoCommit);
            }
            exesvc.shutdown();
        }
    }

    PgsqlSnapshotRecordCreateSlot sendCreateSlot(PgReplSlotTuple tuple, PgsqlSnapshotMetric metric) throws Exception;

    PgsqlSnapshotRecordLogicalMsg sendLogicalMsg(long lsn, LogicalMsg msg, PgsqlSnapshotMetricSelectTupleval metric) throws Exception;

    PgsqlSnapshotRecordPauseWorld sendPauseWorld(PgsqlSnapshotMetricCreateReplSlot metric) throws Exception;

    ImmutableList<PgReplRelation> selectRelalist(PgConnection pgdata) throws Exception;

    boolean lockedRelation(Statement s, PgReplRelation relation) throws Exception;

    PgReplSlotTuple createReplSlot(PgConnection pgrepl) throws Exception;

    String getTupleValSql(PgReplRelation relation) throws Exception;

    PgConnection replConnection() throws SQLException;

    PgConnection dataConnection() throws SQLException;

    long getWaitTimeout();

    long getLogDuration();

    static <T> T pollFromFuture(Future<T> future, long waitTimeout) //
        throws SQLException, InterruptedException
    {
        try {
            return future.get(waitTimeout, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException ex) {
            return null;
        }
        catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            else if (cause instanceof SQLException) {
                throw (SQLException)cause;
            }
            else {
                // TODO:
                throw new RuntimeException(cause);
            }
        }
    }

    default <T> T pollFromFuture(Future<T> future) throws Exception
    {
        return pollFromFuture(future, this.getWaitTimeout());
    }

    PgsqlSnapshotMetricSelectRelalist newSelectRelalist(ZonedDateTime startMillis, long retryCounts);
}
