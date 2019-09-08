/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricRunRelaLock implements MainlineMetricRun
{
    static MainlineMetricRunRelaLock of(MainlineActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricRunRelaLock(action);
    }

    private MainlineMetricRunRelaLock(MainlineActionDataRelaLock action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = MainlineReportRelaLock.of(action, finish);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode relalistNode = node.putObject("relalist");
        this.relalist.toJsonObject(relalistNode);
        ObjectNode relalockNode = node.putObject("relalock");
        this.relalock.toJsonObject(relalockNode);
    }
}
