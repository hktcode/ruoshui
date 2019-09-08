/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrTypelistStraight extends MainlineMetricErrTypelist
{
    static MainlineMetricErrTypelistStraight of(MainlineMetricEndTypelistStraight metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineMetricErrTypelistStraight(metric, throwerr);
    }

    private MainlineMetricErrTypelistStraight(MainlineMetricEndTypelistStraight metric, Throwable throwerr)
    {
        super(metric, throwerr);
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode typelistNode = node.putObject("typelist");
        this.typelist.toJsonObject(typelistNode);
        ObjectNode throwerrNode = node.putObject("throwerr");
        this.throwerr.toJsonObject(throwerrNode);
    }
}
