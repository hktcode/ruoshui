/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PgActionDataSizeDiff extends PgActionDataQueryRel
{
    static PgActionDataSizeDiff of(PgActionDataReplSlot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSizeDiff(action);
    }

    private static final Logger logger = LoggerFactory.getLogger(PgActionDataSizeDiff.class);

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relaLock;

    public final PgReportReplSlotTuple replSlot;

    final ImmutableList<PgStructRelainfo> oldRelalist;

    private PgActionDataSizeDiff(PgActionDataReplSlot action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = PgReportReplSlotTuple.of(action, this.actionStart);
        this.oldRelalist = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    @Override
    PgAction complete(Connection pgdata) throws SQLException
    {
        if (super.relaBuilder[0] != null) {
            this.newRelalist.add(super.relaBuilder[0].builder());
        }
        if (this.newRelalist.size() == this.oldRelalist.size()) {
            return PgActionDataSsBegins.of(this);
        }
        else {
            pgdata.rollback();
            return PgActionDataRelaList.of(this);
        }
    }

    @Override
    PgDeputeStatement createDepute(PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException
    {
        String name = this.replSlot.createTuple.snapshotName;
        String setTransaction //
            = "SET TRANSACTION SNAPSHOT '" + pgdata.escapeLiteral(name) + "'";
        try (Statement s = pgdata.createStatement()) {
            logger.info("execute set snapshot: {}", setTransaction);
            s.execute(setTransaction); // TODO: pollFromFuture?
        }
        return PgDeputeSelectData.of(this.queryRelalist(pgdata));
    }

    @Override
    public PgMetricRunSizeDiff toRunMetrics()
    {
        return PgMetricRunSizeDiff.of(this);
    }
}
