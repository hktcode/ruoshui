/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

import static java.sql.Connection.TRANSACTION_REPEATABLE_READ;

public class Snapshot implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(Snapshot.class);

    public static Snapshot of
        /* */( SnapshotConfig config //
        /* */, AtomicReference<SimpleStatus> status
        /* */, TransferQueue<PgRecord> tqueue
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
        return new Snapshot(config, status, tqueue);
    }


    private final SnapshotConfig config;

    private final AtomicReference<SimpleStatus> status;

    private final TransferQueue<PgRecord> tqueue;

    private Snapshot //
        /* */( SnapshotConfig config //
        /* */, AtomicReference<SimpleStatus> status
        /* */, TransferQueue<PgRecord> tqueue
        /* */)
    {
        this.config = config;
        this.status = status;
        this.tqueue = tqueue;
    }

    public void run()
    {
        logger.info("snapshot starts.");
        try {
            this.runWithInterrupted();
        }
        catch (InterruptedException ex) {
            logger.error("should not be interrupted by other thread.");
            Thread.currentThread().interrupt();
        } catch (ScriptException e) {
            logger.error("should never happen", e);
        }
        logger.info("snapshot finish.");
    }

    private void runWithInterrupted() throws InterruptedException, ScriptException
    {
        PgsenderAction<PgRecord, SnapshotConfig> action
            = PgsenderActionDataRelaList.of(config, status, tqueue);
        try (Connection repl = config.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            ExecutorService exesvc = Executors.newSingleThreadExecutor();
            try (Connection data = config.srcProperty.queriesConnection()) {
                PgConnection pgdata = data.unwrap(PgConnection.class);
                pgdata.setAutoCommit(false);
                pgdata.setTransactionIsolation(TRANSACTION_REPEATABLE_READ);
                do {
                    PgsenderActionData<PgRecord, SnapshotConfig> dataAction //
                        = (PgsenderActionData<PgRecord, SnapshotConfig>)action;
                    action = dataAction.next(exesvc, pgdata, pgrepl);
                } while (action instanceof PgsenderActionData);
            }
            finally {
                exesvc.shutdown();
            }
            logger.info("snapshot completes");
        }
        catch (InterruptedException ex) {
            throw ex;
        }
        // catch (PSQLException ex) {
        //     String sqlState = ex.getSQLState();
        //     ImmutableList<String> cancelStates //
        //         = ImmutableList.of("57014", "55000", "08006");
        //     if (cancelStates.contains(sqlState)) {
        //         // 如果是被Connection取消，属于业务范畴
        //         logger.info("statement was canceled: ", ex);
        //         return;
        //     }
        //     throw ex;
        // }
        catch (Exception ex) {
            logger.error("snapshot throws exception: ", ex);
            action = action.next(ex);
            PgRecord r //
                = PgRecordExecThrows.of(ex);
            do {
                r = action.send(r);
            } while (r != null);
        }
        SimpleStatusInner o;
        SimpleStatusInnerEnd f;
        do {
            o = action.newStatus(action);
            f = SimpleStatusInnerEnd.of(ImmutableList.of(action.del()));
        } while (!this.status.compareAndSet(o, f));
        logger.info("snapshot terminate");
    }
}
