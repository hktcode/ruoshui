/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrSsFinish implements MainlineMetricErr
{
    static MainlineMetricErrSsFinish of(MainlineMetricEndSsFinish metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrSsFinish(metric, throwerr);
    }

    private MainlineMetricErrSsFinish(MainlineMetricEndSsFinish metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = metric.begin1st;
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.replslot = metric.replslot;
        this.sizediff = metric.sizediff;
        this.ssbegins = metric.ssbegins;
        this.tupleval = metric.tupleval;
        this.ssfinish = metric.ssfinish;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportSizeDiff sizediff;

    public final MainlineReportSsBegins ssbegins;

    public final MainlineReportTupleval tupleval;

    public final MainlineReportSsFinish ssfinish;

    public final MainlineReportThrowErr throwerr;

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode begin1stNode = node.putObject("begin1st");
        this.begin1st.toJsonObject(begin1stNode);
        ObjectNode relalistNode = node.putObject("relalist");
        this.relalist.toJsonObject(relalistNode);
        ObjectNode relalockNode = node.putObject("relalock");
        this.relalock.toJsonObject(relalockNode);
        ObjectNode replslotNode = node.putObject("replslot");
        this.replslot.toJsonObject(replslotNode);
        ObjectNode sizediffNode = node.putObject("sizediff");
        this.sizediff.toJsonObject(sizediffNode);
        ObjectNode ssbeginsNode = node.putObject("ssbegins");
        this.ssbegins.toJsonObject(ssbeginsNode);
        ObjectNode tuplevalNode = node.putObject("tupleval");
        this.tupleval.toJsonObject(tuplevalNode);
        ObjectNode ssfinishNode = node.putObject("ssfinish");
        this.ssfinish.toJsonObject(ssfinishNode);
        ObjectNode throwerrNode = node.putObject("throwerr");
        this.throwerr.toJsonObject(throwerrNode);
    }
}
