/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.postgresql.core.Utils;
import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class MainlineActionDataReplSlot //
    extends MainlineActionData<MainlineActionDataReplSlot, MainlineConfigSnapshot>
{
    static MainlineActionDataReplSlot of(MainlineActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataReplSlot(action);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    // public long createStart = 0;
    public long sltDuration = 0;

    public PgReplSlotTuple[] createTuple = new PgReplSlotTuple[0];

    private MainlineActionDataReplSlot(MainlineActionDataRelaLock action)
    {
        super(action, System.currentTimeMillis());
        this.begin1st = action.begin1st;
        this.relalist = action.relalist;
        this.relaLock = MainlineReportRelaLock.of(action, this.actionStart);
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    @Override
    MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl)
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
                    starts = System.currentTimeMillis();
                    tupleFuture = exesvc.submit(callable);
                }
                else if (tuple == null) {
                    tuple = this.pollFromFuture(tupleFuture);
                }
                else if (sendCreateSlot == null) {
                    long finish = System.currentTimeMillis();
                    this.sltDuration = finish - starts;
                    this.createTuple = new PgReplSlotTuple[] { tuple };
                    starts = finish;
                    sendCreateSlot = this.sendCreateSlot(tuple);
                }
                else if (!sendCreateSlot) {
                    sendCreateSlot = this.sendCreateSlot(tuple);
                }
                else {
                    long finish = System.currentTimeMillis();
                    // TODO: logger
                    return MainlineActionDataSizeDiff.of(this);
                }
            }
        }
        return MainlineActionTerminateEnd.of(this);
    }

    private String buildCreateSlotStatement() throws SQLException
    {
        String slotnameInf = config.logicalRepl.slotName;
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

    @Override
    public MainlineMetricRunReplSlot toRunMetrics()
    {
        return MainlineMetricRunReplSlot.of(this);
    }

    @Override
    public MainlineMetricEndReplSlot toEndMetrics()
    {
        return MainlineMetricEndReplSlot.of(this);
    }
}
