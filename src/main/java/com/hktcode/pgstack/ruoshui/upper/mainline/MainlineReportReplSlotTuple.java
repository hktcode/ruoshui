/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;

public class MainlineReportReplSlotTuple extends MainlineReportReplSlot
{
    static MainlineReportReplSlotTuple of(MainlineActionDataReplSlot action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineReportReplSlotTuple(action, finish);
    }

    public final PgReplSlotTuple createTuple;

    private MainlineReportReplSlotTuple(MainlineActionDataReplSlot action, long finish)
    {
        super(action, finish);
        this.createTuple = action.createTuple[0];
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        super.toJsonObject(node);
        ObjectNode createTupleNode = node.putObject("create_tuple");
        this.createTuple.toJsonObject(createTupleNode);
    }
}
