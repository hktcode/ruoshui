/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrReplSlot implements MainlineMetricErr
{
    static MainlineMetricErrReplSlot of(MainlineMetricEndReplSlot action, Throwable throwerr)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrReplSlot(action, throwerr);
    }

    private MainlineMetricErrReplSlot(MainlineMetricEndReplSlot action, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = action.begin1st;
        this.relalist = action.relalist;
        this.relalock = action.relalock;
        this.replslot = action.replslot;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportThrowErr throwerr;

    @Override
    public void toJsonObject(ObjectNode node)
    {
        ObjectNode begin1stNode = node.putObject("begin1st");
        this.begin1st.toJsonObject(begin1stNode);
        ObjectNode relalistNode = node.putObject("relalist");
        this.relalist.toJsonObject(relalistNode);
        ObjectNode relalockNode = node.putObject("relalock");
        this.relalock.toJsonObject(relalockNode);
        ObjectNode replslotNode = node.putObject("replslot");
        this.replslot.toJsonObject(replslotNode);
        ObjectNode throwerrNode = node.putObject("throwerr");
        this.throwerr.toJsonObject(throwerrNode);
    }
}
