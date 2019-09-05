/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
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

public class Mainline implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(Mainline.class);

    public static Mainline of //
        /* */( MainlineConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
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
        return new Mainline(config, status, tqueue);
    }

    public final MainlineConfig config;

    public final AtomicReference<SimpleStatus> status;

    public final TransferQueue<MainlineRecord> tqueue;

    private Mainline //
        /* */( MainlineConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        this.config = config;
        this.status = status;
        this.tqueue = tqueue;
    }

    @Override
    public void run()
    {
        logger.info("mainline starts.");
        try {
            this.runWithInterrupted();
        }
        catch (InterruptedException ex) {
            logger.error("should not be interrupted by other thread.");
            Thread.currentThread().interrupt();
        }
        logger.info("mainline finish.");
    }

    private void runWithInterrupted() throws InterruptedException
    {
        MainlineAction action = config.createsAction(status, tqueue);
        try (Connection repl = config.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            ExecutorService exesvc = Executors.newSingleThreadExecutor();
            try (Connection data = config.srcProperty.queriesConnection()) {
                PgConnection pgdata = data.unwrap(PgConnection.class);
                pgdata.setAutoCommit(false);
                pgdata.setTransactionIsolation(TRANSACTION_REPEATABLE_READ);
                do {
                    MainlineActionData dataAction = (MainlineActionData)action;
                    action = dataAction.next(exesvc, pgdata, pgrepl);
                } while (action instanceof MainlineActionData);
            }
            finally {
                exesvc.shutdown();
            }
            while (action instanceof MainlineActionRepl) {
                action = ((MainlineActionRepl) action).next(pgrepl);
            }
            logger.info("mainline completes");
        }
        catch (InterruptedException ex) {
            throw ex;
        }
        catch (Exception ex) {
            logger.error("mainline throws exception: ", ex);
            action = action.nextThrowErr(ex);
        }
        SimpleStatusInner o;
        SimpleStatusInnerEnd f;
        do {
            o = action.newStatus(action);
            f = SimpleStatusInnerEnd.of(ImmutableList.of(action.del()));
        } while (!this.status.compareAndSet(o, f));
        logger.info("mainline terminate");
    }
}
