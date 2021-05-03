package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.simple.SimpleMetric;

public class UpcsmMetric extends SimpleMetric
{
    public static UpcsmMetric of()
    {
        return new UpcsmMetric();
    }

    private UpcsmMetric()
    {
        this.actionStart = System.currentTimeMillis();
    }
}
