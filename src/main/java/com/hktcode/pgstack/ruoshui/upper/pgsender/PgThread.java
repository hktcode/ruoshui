/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

import static java.sql.Connection.TRANSACTION_REPEATABLE_READ;

public class PgThread implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(PgThread.class);

    public static PgThread of //
        /* */( PgConfig config //
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
        return new PgThread(config, status, tqueue);
    }

    public final PgConfig config;

    public final AtomicReference<SimpleStatus> status;

    public final TransferQueue<PgRecord> tqueue;

    private PgThread //
        /* */( PgConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        this.config = config;
        this.status = status;
        this.tqueue = tqueue;
    }

    @Override
    public void run()
    {
        logger.info("pgsender starts.");
        try {
            this.runWithInterrupted();
        }
        catch (InterruptedException ex) {
            logger.error("should not be interrupted by other thread.");
            Thread.currentThread().interrupt();
        }
        logger.info("pgsender finish.");
    }

    private void runWithInterrupted() throws InterruptedException
    {
        PgAction action = this.config.createsAction(status, tqueue);
        try (Connection repl = config.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            ExecutorService exesvc = Executors.newSingleThreadExecutor();
            try (Connection data = config.srcProperty.queriesConnection()) {
                PgConnection pgdata = data.unwrap(PgConnection.class);
                pgdata.setAutoCommit(false);
                pgdata.setTransactionIsolation(TRANSACTION_REPEATABLE_READ);
                do {
                    PgActionData
                        dataAction = (PgActionData)action;
                    action = dataAction.next(exesvc, pgdata, pgrepl);
                } while (action instanceof PgActionData);
            }
            finally {
                exesvc.shutdown();
            }
            while (action instanceof PgActionRepl) {
                action = ((PgActionRepl) action).next(pgrepl);
            }
            logger.info("pgsender complete");
        }
        catch (InterruptedException ex) {
            throw ex;
        }
        catch (Exception ex) {
            logger.error("pgsender throwerr: ", ex);
            action = action.next(ex);
        }
        while (!(action instanceof PgActionTerminateEnd)) {
            action = action.next();
        }
        logger.info("pgsender terminate");
    }
}