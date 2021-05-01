package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicLong;

public class UppdcMetricKafka extends UppdcMetric
{
    public static UppdcMetricKafka of(AtomicLong txactionLsn)
    {
        if (txactionLsn == null) {
            throw new ArgumentNullException("txactionLsn");
        }
        return new UppdcMetricKafka(txactionLsn);
    }

    private UppdcMetricKafka(AtomicLong txactionLsn)
    {
        super(txactionLsn);
    }
}
