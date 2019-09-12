/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.PgReplRelation;

import java.util.ArrayList;
import java.util.List;

abstract class PgActionDataSnapshot extends PgActionDataOfferMsg
{
    public final PgReportRelaList relalist;

    public final PgReportRelaLock relaLock;

    public final PgReportReplSlotTuple replSlot;

    public final PgReportSizeDiff sizeDiff;

    final ImmutableList<PgStructRelainfo> relationLst;

    PgActionDataSnapshot(PgActionDataSizeDiff action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = PgReportSizeDiff.of(action, this.actionStart);
        this.relationLst = action.oldRelalist;
        this.logDatetime = action.logDatetime;
    }

    PgActionDataSnapshot(PgActionDataSnapshot action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    PgActionDataSnapshot(PgActionDataSrFinish action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.relationLst = action.relationLst;
        this.logDatetime = action.logDatetime;
    }

    @Override
    PgRecord createRecord()
    {
        long lsn = this.replSlot.createTuple.consistentPoint;
        List<PgReplRelation> list = new ArrayList<>(this.relationLst.size());
        for(PgStructRelainfo m : this.relationLst) {
            list.add(m.relationInfo);
        }
        ImmutableList<PgReplRelation> l = ImmutableList.copyOf(list);
        LogicalMsg msg = this.createMsg(l);
        return this.config.createMessage(lsn, msg);
    }

    abstract LogicalMsg createMsg(ImmutableList<PgReplRelation> list);
}
