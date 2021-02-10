package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.simple.SimpleMetric;

import java.util.concurrent.atomic.AtomicReference;

public class UppdcMetric extends SimpleMetric
{
    public static UppdcMetric of()
    {
        return new UppdcMetric();
    }

    public final AtomicReference<Exception> callbackRef = new AtomicReference<>();

    private UppdcMetric()
    {
    }
}
