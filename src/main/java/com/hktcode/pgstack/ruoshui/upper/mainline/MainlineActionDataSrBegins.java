/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegRelationMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecordNormal;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

class MainlineActionDataSrBegins //
    extends MainlineActionData<MainlineConfig>
{
    static MainlineActionDataSrBegins of(MainlineActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataSrBegins(action);
    }

    static MainlineActionDataSrBegins of(MainlineActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataSrBegins(action);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final MainlineReportReplSlotTuple replSlot;

    public final MainlineReportSizeDiff sizeDiff;

    public final MainlineReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    final PgsqlRelationMetric curRelation;

    private MainlineActionDataSrBegins(MainlineActionDataSsBegins action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssbegins = MainlineReportSsBegins.of(action, action.actionStart);
        this.relationLst = action.relationLst;
        this.relIterator = action.relIterator;
        this.curRelation = this.relIterator.next();
        this.logDatetime = action.logDatetime;
    }

    private MainlineActionDataSrBegins(MainlineActionDataSrFinish action) //
    {
        super(action, action.actionStart);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.relationLst = action.relationLst;
        this.relIterator = action.relIterator;
        this.logDatetime = action.logDatetime;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.offerMillis = action.offerMillis;
        this.offerCounts = action.offerCounts;
        this.rsnextCount = action.rsnextCount;
        this.recordCount = action.recordCount;
        this.curRelation = action.relIterator.next();
    }


    @Override
    public MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException, ScriptException
    {
        PgReplRelation r = this.curRelation.relationInfo;
        long lsn = this.replSlot.createTuple.consistentPoint;
        LogicalBegRelationMsg msg = LogicalBegRelationMsg.of(r);
        MainlineRecord record = MainlineRecordNormal.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return MainlineActionDataTupleval.of(this);
            }
        }
        return MainlineActionTerminateEnd.of(this);
    }

    @Override
    public MainlineMetricRunTupleval toRunMetrics()
    {
        return MainlineMetricRunTupleval.of(this);
    }

    @Override
    public MainlineMetricEndTupleval toEndMetrics()
    {
        return MainlineMetricEndTupleval.of(this);
    }
}
