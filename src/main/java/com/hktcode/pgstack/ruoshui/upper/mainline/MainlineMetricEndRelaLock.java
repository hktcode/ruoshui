/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricEndRelaLock implements MainlineMetricEnd
{
    static MainlineMetricEndRelaLock of(MainlineActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricEndRelaLock(action);
    }

    private MainlineMetricEndRelaLock(MainlineActionDataRelaLock action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = MainlineReportRelaLock.of(action, finish);
        this.complete = MainlineReportComplete.of(finish);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportComplete complete;

    @Override
    public MainlineMetricErrRelaLock toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return MainlineMetricErrRelaLock.of(this, throwerr);
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
