/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricRunTxactionStraight extends MainlineMetricRunTxaction
{
    static MainlineMetricRunTxactionStraight of(MainlineActionReplTxactionStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricRunTxactionStraight(action);
    }

    private MainlineMetricRunTxactionStraight(MainlineActionReplTxactionStraight action)
    {
        super(action);
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
    }
}
