package com.hktcode.pgstack.ruoshui.upper.mainline;

abstract class MainlineAction
{
    final MainlineSender sender;

    abstract MainlineMetric getMetric();

    MainlineAction(MainlineSender sender)
    {
        this.sender = sender;
    }
}
