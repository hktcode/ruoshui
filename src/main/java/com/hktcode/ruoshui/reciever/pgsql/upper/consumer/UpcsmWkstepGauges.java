package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.simple.SimpleWkstepGauges;

public class UpcsmWkstepGauges extends SimpleWkstepGauges
{
    public static UpcsmWkstepGauges of()
    {
        return new UpcsmWkstepGauges();
    }

    private UpcsmWkstepGauges()
    {
        this.wkstepStart = System.currentTimeMillis();
    }
}
