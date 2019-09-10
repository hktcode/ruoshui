/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

class PgActionDataSsBegins extends PgActionData
{
    static PgActionDataSsBegins of(PgActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataSsBegins(action);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relaLock;

    public final PgReportReplSlotTuple replSlot;

    public final PgReportSizeDiff sizeDiff;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    private PgActionDataSsBegins(PgActionDataSizeDiff action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = PgReportSizeDiff.of(action, this.actionStart);
        this.relationLst = action.oldRelalist;
        this.relIterator = this.relationLst.iterator();
        this.logDatetime = action.logDatetime;
    }

    @Override
    public PgAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        List<PgReplRelation> list = new ArrayList<>(this.relationLst.size());
        for(PgsqlRelationMetric m : this.relationLst) {
            list.add(m.relationInfo);
        }
        ImmutableList<PgReplRelation> l = ImmutableList.copyOf(list);
        LogicalBegSnapshotMsg msg = LogicalBegSnapshotMsg.of(l);
        PgRecord record = this.config.createMessage(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if (record != null) {
                record = this.send(record);
            }
            else if (this.relIterator.hasNext()){
                return PgActionDataSrBegins.of(this);
            }
            else {
                return PgActionDataSsFinish.of(this);
            }
        }
        return PgActionTerminateEnd.of(this);
    }

    @Override
    public PgMetricRunSsbegins toRunMetrics()
    {
        return PgMetricRunSsbegins.of(this);
    }

    @Override
    public PgMetricEndSsbegins toEndMetrics()
    {
        return PgMetricEndSsbegins.of(this);
    }
}
