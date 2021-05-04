package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.simple.SimpleWkstepMetric;

public class UpcsmWkstepMetric extends SimpleWkstepMetric
{
    public static UpcsmWkstepMetric of()
    {
        return new UpcsmWkstepMetric();
    }

    private UpcsmWkstepMetric()
    {
        this.wkstepStart = System.currentTimeMillis();
    }
}
