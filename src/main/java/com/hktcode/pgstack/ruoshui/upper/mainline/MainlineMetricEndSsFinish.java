/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricEndSsFinish implements MainlineMetricEnd
{
    static MainlineMetricEndSsFinish of(MainlineActionDataSsFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricEndSsFinish(action);
    }

    private MainlineMetricEndSsFinish(MainlineActionDataSsFinish action)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = action.begin1st;
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = MainlineReportSsFinish.of(action, finish);
        this.complete = MainlineReportComplete.of(finish);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportSizeDiff sizediff;

    public final MainlineReportSsBegins ssbegins;

    public final MainlineReportTupleval tupleval;

    public final MainlineReportSsFinish ssfinish;

    public final MainlineReportComplete complete;

    @Override
    public MainlineMetricErrSsFinish toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return MainlineMetricErrSsFinish.of(this, throwerr);
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
        ObjectNode ssbeginsNode = node.putObject("ssbegins");
        this.ssbegins.toJsonObject(ssbeginsNode);
        ObjectNode tuplevalNode = node.putObject("tupleval");
        this.tupleval.toJsonObject(tuplevalNode);
        ObjectNode ssfinishNode = node.putObject("ssfinish");
        this.ssfinish.toJsonObject(ssfinishNode);
        ObjectNode completeNode = node.putObject("complete");
        this.complete.toJsonObject(completeNode);
    }
}
