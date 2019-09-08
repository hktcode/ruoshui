/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricEndTxactionStraight extends MainlineMetricEndTxaction
{
    static MainlineMetricEndTxactionStraight of(MainlineActionReplTxactionStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricEndTxactionStraight(action);
    }

    private MainlineMetricEndTxactionStraight(MainlineActionReplTxactionStraight action)
    {
        super(action);
    }

    @Override
    public MainlineMetricErrTxactionStraight toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return MainlineMetricErrTxactionStraight.of(this, throwerr);
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode typelistNode = node.putObject("typelist");
        this.typelist.toJsonObject(typelistNode);
        ObjectNode txactionNode = node.putObject("txaction");
        this.txaction.toJsonObject(txactionNode);
        ObjectNode completeNode = node.putObject("complete");
        this.complete.toJsonObject(completeNode);
    }
}
