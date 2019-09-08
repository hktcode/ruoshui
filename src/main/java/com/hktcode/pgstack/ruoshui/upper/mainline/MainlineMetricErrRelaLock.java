/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrRelaLock implements MainlineMetricErr
{
    static MainlineMetricErrRelaLock of(MainlineMetricEndRelaLock metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrRelaLock(metric, throwerr);
    }

    private MainlineMetricErrRelaLock(MainlineMetricEndRelaLock metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportThrowErr throwerr;

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode relalistNode = node.putObject("relalist");
        this.relalist.toJsonObject(relalistNode);
        ObjectNode relalockNode = node.putObject("relalock");
        this.relalock.toJsonObject(relalockNode);
        ObjectNode throwerrNode = node.putObject("throwerr");
        this.throwerr.toJsonObject(throwerrNode);
    }
}
