/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrSizeDiff implements MainlineMetricErr
{
    static MainlineMetricErrSizeDiff of(MainlineMetricEndSizeDiff metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("action");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrSizeDiff(metric, throwerr);
    }

    private MainlineMetricErrSizeDiff(MainlineMetricEndSizeDiff metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.replslot = metric.replslot;
        this.sizediff = metric.sizediff;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportSizeDiff sizediff;

    public final MainlineReportThrowErr throwerr;

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode relalistNode = node.putObject("relalist");
        this.relalist.toJsonObject(relalistNode);
        ObjectNode relalockNode = node.putObject("relalock");
        this.relalock.toJsonObject(relalockNode);
        ObjectNode replslotNode = node.putObject("replslot");
        this.replslot.toJsonObject(replslotNode);
        ObjectNode sizediffNode = node.putObject("sizediff");
        this.sizediff.toJsonObject(sizediffNode);
        ObjectNode throwerrNode = node.putObject("throwerr");
        this.throwerr.toJsonObject(throwerrNode);
    }
}
