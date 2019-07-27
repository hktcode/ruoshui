/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

abstract class MainlineStep
{
    final MainlineSender sender;

    abstract MainlineMetric getMetric();

    MainlineStep(MainlineSender sender)
    {
        this.sender = sender;
    }
}
