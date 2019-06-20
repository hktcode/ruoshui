/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;

public class PgSnapshot<M> implements Runnable
{
    public static<M> PgSnapshot<M> of //
        /* */( PgSnapshotConfig config
        /* */, PgConnection pgrepl
        /* */, PgConnection pgdata
        /* */, PgSnapshotSender<M> sender
        /* */) //
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        return new PgSnapshot<>(config, pgrepl, pgdata, sender);
    }

    private static final Logger logger = LoggerFactory.getLogger(PgSnapshot.class);

    private final PgSnapshotConfig config;

    private final PgConnection pgrepl;

    private final PgConnection pgdata;

    private final PgSnapshotSender<M> sender;

    private PgSnapshot //
        /* */( PgSnapshotConfig config //
        /* */, PgConnection pgrepl //
        /* */, PgConnection pgdata //
        /* */, PgSnapshotSender<M> sender //
        /* */) //
    {
        this.config = config;
        this.pgrepl = pgrepl;
        this.pgdata = pgdata;
        this.sender = sender;
    }

    public void run()
    {
        try {
            this.runWithInterrupted();
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            // 被外部程序打断，属于业务范畴
            logger.info("interrupted by other thread.");
        }
    }

    private void runWithInterrupted() throws InterruptedException
    {
        ZonedDateTime startMillis = ZonedDateTime.now();
        M metric = sender.snapshotMetric(startMillis);
        try {
            this.runInternal(metric);
        }
        catch (InterruptedException ex) {
            throw ex;
        }
        catch (Exception ex) {
            logger.error("getting snapshot throws exception: ", ex);
            this.sender.sendExecThrows(ex, config.logDuration, metric);
        }
    }

    private void runInternal(M metric) //
        throws InterruptedException, SQLException, ScriptException
    {
        try {
            PgConnection c = this.pgdata;
            c.setReadOnly(true);
            c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            c.setAutoCommit(false);
            PgSnapshotStep<M> step;
            do {
                this.sender.sendStatusInfo("query old relation list", metric);
                ImmutableList<PgReplRelation> l = config.queryForRelations(c);
                c.commit();
                step = PgSnapshotStepLockedRelalist.of(config, c, pgrepl, l);
                do {
                    step = ((PgSnapshotStepProcess<M>) step).next(metric, sender);
                } while(step instanceof PgSnapshotStepProcess);
            } while (!((PgSnapshotStepFinish)step).finish(c));
            this.sender.sendExecFinish(this.config.logDuration, metric);
        }
        catch (PSQLException ex) {
            String sqlState = ex.getSQLState();
            ImmutableList<String> cancelStates //
                = ImmutableList.of("57014", "55000", "08006");
            if (cancelStates.contains(sqlState)) {
                // 如果是被Connection取消，属于业务范畴
                logger.info("statement was canceled: ", ex);
                return;
            }
            throw ex;
        }
    }
}
