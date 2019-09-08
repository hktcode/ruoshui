/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrRelaList implements MainlineMetricErr
{
    static MainlineMetricErrRelaList //
    of(MainlineMetricEndRelaList metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineMetricErrRelaList(metric, throwerr);
    }

    private MainlineMetricErrRelaList(MainlineMetricEndRelaList metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportThrowErr throwerr;

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode relalistNode = node.putObject("relalist");
        this.relalist.toJsonObject(relalistNode);
        ObjectNode throwerrNode = node.putObject("throwerr");
        this.throwerr.toJsonObject(throwerrNode);
    }
}
