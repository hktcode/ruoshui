/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import org.postgresql.jdbc.PgConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

class MainlineActionDataSsBegins //
    extends MainlineActionData<MainlineConfigSnapshot>
{
    static MainlineActionDataSsBegins of(MainlineActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataSsBegins(action);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final MainlineReportReplSlot replSlot;

    public final MainlineReportSizeDiff sizeDiff;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    private MainlineActionDataSsBegins(MainlineActionDataSizeDiff action)
    {
        super(action, System.currentTimeMillis());
        this.begin1st = action.begin1st;
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = MainlineReportSizeDiff.of(action, this.actionStart);
        this.relationLst = action.oldRelalist;
        this.relIterator = this.relationLst.iterator();
        this.logDatetime = action.logDatetime;
    }

    @Override
    public MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        List<PgReplRelation> list = new ArrayList<>(this.relationLst.size());
        for(PgsqlRelationMetric m : this.relationLst) {
            list.add(m.relationInfo);
        }
        ImmutableList<PgReplRelation> l = ImmutableList.copyOf(list);
        LogicalBegSnapshotMsg msg = LogicalBegSnapshotMsg.of(l);
        MainlineRecord record = MainlineRecordNormal.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if (record != null) {
                record = this.send(record);
            }
            else if (this.relIterator.hasNext()){
                return MainlineActionDataSrBegins.of(this);
            }
            else {
                return MainlineActionDataSsFinish.of(this);
            }
        }
        return MainlineActionTerminateEnd.of(this);
    }

    @Override
    public MainlineMetricRunSsbegins toRunMetrics()
    {
        return MainlineMetricRunSsbegins.of(this);
    }

    @Override
    public MainlineMetricEndSsbegins toEndMetrics()
    {
        return MainlineMetricEndSsbegins.of(this);
    }
}
