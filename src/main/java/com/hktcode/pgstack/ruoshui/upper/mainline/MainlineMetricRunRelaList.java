/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricRunRelaList implements MainlineMetricRun
{
    static MainlineMetricRunRelaList of(MainlineActionDataRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricRunRelaList(action);
    }

    private MainlineMetricRunRelaList(MainlineActionDataRelaList action)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = action.begin1st;
        this.relalist = MainlineReportRelaList.of(action, finish);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode begin1stNode = node.putObject("begin1st");
        this.begin1st.toJsonObject(begin1stNode);
        ObjectNode relalistNode = node.putObject("relalist");
        this.relalist.toJsonObject(relalistNode);
    }
}
