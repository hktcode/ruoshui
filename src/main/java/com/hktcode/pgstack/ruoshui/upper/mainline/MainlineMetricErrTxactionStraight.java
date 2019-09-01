/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrTxactionStraight extends MainlineMetricErrTxaction
{
    static MainlineMetricErrTxactionStraight of(MainlineMetricEndTxactionStraight metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrTxactionStraight(metric, throwerr);
    }

    private MainlineMetricErrTxactionStraight(MainlineMetricEndTxactionStraight metric, Throwable throwerr)
    {
        super(metric, throwerr);
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode begin1stNode = node.putObject("begin1st");
        this.begin1st.toJsonObject(begin1stNode);
        ObjectNode typelistNode = node.putObject("typelist");
        this.typelist.toJsonObject(typelistNode);
        ObjectNode txactionNode = node.putObject("txaction");
        this.txaction.toJsonObject(txactionNode);
        ObjectNode throwerrNode = node.putObject("throwerr");
        this.throwerr.toJsonObject(throwerrNode);
    }
}
