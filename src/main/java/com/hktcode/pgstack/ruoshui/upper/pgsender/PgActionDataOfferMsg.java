/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.pgjdbc.PgReplRelation;
import org.postgresql.jdbc.PgConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

abstract class PgActionDataOfferMsg extends PgActionData
{
    final PgReportRelaList relalist;

    final PgReportRelaLock relaLock;

    final PgReportReplSlotTuple replSlot;

    final PgReportSizeDiff sizeDiff;

    final ImmutableList<PgStructRelainfo> relationLst;

    PgActionDataOfferMsg(PgActionDataOfferMsg action, long actionStart)
    {
        super(action, actionStart);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    PgActionDataOfferMsg(PgActionDataOfferMsg action)
    {
        super(action, action.actionStart);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    PgActionDataOfferMsg(PgActionDataTupleval action)
    {
        super(action, action.actionStart);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    PgActionDataOfferMsg(PgActionDataSizeDiff action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = PgReportSizeDiff.of(action, this.actionStart);
        this.relationLst = action.oldRelalist;
        this.logDatetime = action.logDatetime;
    }

    @Override
    public PgAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws InterruptedException
    {
        PgRecord record = this.createRecord();
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            if ((record = this.send(record)) == null) {
                return this.complete();
            }
        }
        return PgActionTerminateEnd.of(this);
    }

    ImmutableList<PgReplRelation> getImmutableReplRelaList()
    {
        List<PgReplRelation> list = new ArrayList<>(this.relationLst.size());
        for(PgStructRelainfo m : this.relationLst) {
            list.add(m.relationInfo);
        }
        return ImmutableList.copyOf(list);
    }

    abstract PgRecord createRecord();

    abstract PgAction complete();
}
