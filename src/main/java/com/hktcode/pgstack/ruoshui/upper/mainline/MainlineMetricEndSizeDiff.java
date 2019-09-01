/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricEndSizeDiff implements MainlineMetricEnd
{
    static MainlineMetricEndSizeDiff of(MainlineActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricEndSizeDiff(action);
    }

    private MainlineMetricEndSizeDiff(MainlineActionDataSizeDiff action)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = action.begin1st;
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = MainlineReportSizeDiff.of(action, finish);
        this.complete = MainlineReportComplete.of(finish);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportSizeDiff sizediff;

    public final MainlineReportComplete complete;

    @Override
    public MainlineMetricErrSizeDiff toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return MainlineMetricErrSizeDiff.of(this, throwerr);
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode begin1stNode = node.putObject("begin1st");
        this.begin1st.toJsonObject(begin1stNode);
        ObjectNode relalistNode = node.putObject("relalist");
        this.relalist.toJsonObject(relalistNode);
        ObjectNode relalockNode = node.putObject("relalock");
        this.relalock.toJsonObject(relalockNode);
        ObjectNode sizediffNode = node.putObject("sizediff");
        this.sizediff.toJsonObject(sizediffNode);
        ObjectNode completeNode = node.putObject("complete");
        this.complete.toJsonObject(completeNode);
    }
}
