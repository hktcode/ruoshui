/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.core.Utils;
import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionNormalReplSlot extends MainlineActionNormal
    /* */< MainlineActionNormalReplSlot
    /* */, MainlineConfigNormal
    /* */, MainlineMetricNormalReplSlot
    /* */>
{
    // private static final Logger logger = LoggerFactory.getLogger(MainlineActionReplSlot.class);

    static MainlineActionNormalReplSlot of(MainlineActionNormalRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        long finish = System.currentTimeMillis();
        MainlineReportRelaList relalist = action.metric.getRelalist;
        MainlineReportRelaLock relaLock = MainlineReportRelaLock.of(
            finish - action.metric.actionStart
        );
        MainlineMetricNormalReplSlot metric = MainlineMetricNormalReplSlot.of //
            /* */( action.metric.startMillis //
            /* */, finish //
            /* */, action.metric.relationLst //
            /* */, relalist //
            /* */, relaLock //
            /* */);
        return new MainlineActionNormalReplSlot //
            /* */( action.config //
            /* */, metric //
            /* */, action.tqueue //
            /* */, action.status //
            /* */);
    }


    MainlineActionNormalReplSlot //
        /* */(MainlineConfigNormal config //
        /* */, MainlineMetricNormalReplSlot metric //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        super(config, metric, status, tqueue);
    }

    MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl)
        throws Exception
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
        long starts = System.currentTimeMillis();
        try(Statement s = pgrepl.createStatement()) {
            boolean sendPauseWorld = this.sendPauseWorld();
            Future<PgReplSlotTuple> tupleFuture = null;
            Boolean sendCreateSlot = null;
            PgReplSlotTuple tuple = null;
            while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
                if (!sendPauseWorld) {
                    sendPauseWorld = this.sendPauseWorld();
                }
                else if (tupleFuture == null) {
                    String sqlscript = this.buildCreateSlotStatement();
                    MainlineDeputeCreateReplSlot callable = MainlineDeputeCreateReplSlot.of(s, sqlscript);
                    tupleFuture = exesvc.submit(callable);
                }
                else if (tuple == null) {
                    tuple = this.pollFromFuture(tupleFuture);
                }
                else if (sendCreateSlot == null) {
                    long finish = System.currentTimeMillis();
                    metric.sltDuration = finish - starts;
                    metric.createTuple = new PgReplSlotTuple[] { tuple };
                    sendCreateSlot = this.sendCreateSlot(tuple);
                }
                else if (!sendCreateSlot) {
                    sendCreateSlot = this.sendCreateSlot(tuple);
                }
                else {
                    return MainlineActionNormalSizeDiff.of(this);
                }
            }
        }
        return MainlineActionFinish.of();
    }

    private String buildCreateSlotStatement() throws SQLException
    {
        String slotnameInf = config.slotnameInf;
        StringBuilder sb = new StringBuilder("CREATE_REPLICATION_SLOT ");
        Utils.escapeIdentifier(sb, slotnameInf);
        sb.append(" LOGICAL pgoutput EXPORT_SNAPSHOT");
        return sb.toString();
    }

    public boolean sendPauseWorld()
    {
        return true;
    }

    public boolean sendCreateSlot(PgReplSlotTuple tuple)
    {
        if (tuple == null) {
            throw new ArgumentNullException("tuple");
        }
        return true;
    }
}
