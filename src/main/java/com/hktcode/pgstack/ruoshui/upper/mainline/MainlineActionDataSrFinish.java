/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndRelationMsg;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecordNormal;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

public class MainlineActionDataSrFinish extends MainlineActionData
{
    static MainlineActionDataSrFinish of(MainlineActionDataTupleval action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataSrFinish(action);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final MainlineReportReplSlotTuple replSlot;

    public final MainlineReportSizeDiff sizeDiff;

    public final MainlineReportSsBegins ssbegins;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    final Iterator<PgsqlRelationMetric> relIterator;

    final PgsqlRelationMetric curRelation;

    private MainlineActionDataSrFinish(MainlineActionDataTupleval action) //
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
        this.curRelation = action.curRelation;
    }

    @Override
    public MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException, ScriptException
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        LogicalEndRelationMsg msg //
            = LogicalEndRelationMsg.of(this.curRelation.relationInfo);
        MainlineRecord record = MainlineRecordNormal.of(lsn, msg);
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if (record != null) {
                record = this.send(record);
            }
            else if (this.relIterator.hasNext()) {
                return MainlineActionDataSrBegins.of(this);
            }
            else {
                return MainlineActionDataSsFinish.of(this);
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
