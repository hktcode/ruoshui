/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrSsbegins implements MainlineMetricErr
{
    static MainlineMetricErrSsbegins of(MainlineMetricEndSsbegins metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrSsbegins(metric, throwerr);
    }

    private MainlineMetricErrSsbegins(MainlineMetricEndSsbegins metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = metric.begin1st;
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.replslot = metric.replslot;
        this.sizediff = metric.sizediff;
        this.ssbegins = metric.ssbegins;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportSizeDiff sizediff;

    public final MainlineReportSsBegins ssbegins;

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
        ObjectNode throwerrNode = node.putObject("throwerr");
        this.throwerr.toJsonObject(throwerrNode);
    }
}
