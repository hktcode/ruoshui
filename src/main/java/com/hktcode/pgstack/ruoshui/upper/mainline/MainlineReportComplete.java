/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineReportComplete implements MainlineReport
{
    public final long actionStart;

    static MainlineReportComplete of(long actionStart)
    {
        return new MainlineReportComplete(actionStart);
    }

    private MainlineReportComplete(long actionStart)
    {
        this.actionStart = actionStart;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("action_start", actionStart);
    }
}
