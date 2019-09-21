/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.Connection;
import java.sql.SQLException;

class PgActionDataReplSlotSnapshot extends PgActionDataReplSlot
{
    static PgActionDataReplSlotSnapshot of(PgActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataReplSlotSnapshot(action);
    }

    final PgReportRelaList relalist;

    final PgReportRelaLock relaLock;

    final ImmutableList<PgStructRelainfo> relationLst;

    private PgActionDataReplSlotSnapshot(PgActionDataRelaLock action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relaLock = PgReportRelaLock.of(action, this.actionStart);
        this.relationLst = action.relationLst;
    }

    @Override
    PgRecord initRecord()
    {
        return PgRecordPauseWorld.of();
    }

    @Override
    PgAction complete(Connection pgdata) throws SQLException
    {
        if (this.createTuple.length == 0) {
            pgdata.commit();
            return PgActionDataTypelistContinue.of(this);
        }
        return PgActionDataSizeDiff.of(this);
    }

    @Override
    public PgMetricRunReplSlot toRunMetrics()
    {
        return PgMetricRunReplSlot.of(this);
    }
}
