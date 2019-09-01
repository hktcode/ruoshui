/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricRunTypelistStraight extends MainlineMetricRunTypelist
{
    static MainlineMetricRunTypelistStraight of(MainlineActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricRunTypelistStraight(action);
    }

    private MainlineMetricRunTypelistStraight(MainlineActionDataTypelistStraight action)
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
    }
}
