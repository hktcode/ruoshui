package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.simple.SimpleMetric;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class UppdcMetric extends SimpleMetric
{
    public static UppdcMetric of(AtomicLong txactionLsn)
    {
        if (txactionLsn == null) {
            throw new ArgumentNullException("txactionLsn");
        }
        return new UppdcMetric(txactionLsn);
    }

    public final AtomicReference<Exception> callbackRef = new AtomicReference<>();

    public final AtomicLong txactionLsn;

    private UppdcMetric(AtomicLong txactionLsn)
    {
        this.txactionLsn = txactionLsn;
    }
}
