/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricRunTypelistSnapshot extends MainlineMetricRunTypelist
{
    static MainlineMetricRunTypelistSnapshot of(MainlineActionDataTypelistSnapshot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricRunTypelistSnapshot(action);
    }

    private MainlineMetricRunTypelistSnapshot(MainlineActionDataTypelistSnapshot action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = action.ssfinish;
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportSizeDiff sizediff;

    public final MainlineReportSsBegins ssbegins;

    public final MainlineReportTupleval tupleval;

    public final MainlineReportSsFinish ssfinish;

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
        ObjectNode ssbeginsNode = node.putObject("ssbegins");
        this.ssbegins.toJsonObject(ssbeginsNode);
        ObjectNode tuplevalNode = node.putObject("tupleval");
        this.tupleval.toJsonObject(tuplevalNode);
        ObjectNode ssfinishNode = node.putObject("ssfinish");
        this.ssfinish.toJsonObject(ssfinishNode);
        ObjectNode typelistNode = node.putObject("typelist");
        this.typelist.toJsonObject(typelistNode);
    }
}
