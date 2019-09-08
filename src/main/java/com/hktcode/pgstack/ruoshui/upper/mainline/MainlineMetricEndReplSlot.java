/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricEndReplSlot implements MainlineMetricEnd
{
    static MainlineMetricEndReplSlot of(MainlineActionDataReplSlot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricEndReplSlot(action);
    }

    private MainlineMetricEndReplSlot(MainlineActionDataReplSlot action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = MainlineReportReplSlot.of(action, finish);
        this.complete = MainlineReportComplete.of(finish);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportComplete complete;

    @Override
    public MainlineMetricErrReplSlot toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return MainlineMetricErrReplSlot.of(this, throwerr);
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode relalistNode = node.putObject("relalist");
        this.relalist.toJsonObject(relalistNode);
        ObjectNode relalockNode = node.putObject("relalock");
        this.relalock.toJsonObject(relalockNode);
        ObjectNode completeNode = node.putObject("complete");
        this.complete.toJsonObject(completeNode);
    }
}
