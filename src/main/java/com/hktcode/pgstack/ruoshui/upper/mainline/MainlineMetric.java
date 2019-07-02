package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;

import java.time.ZonedDateTime;

public class MainlineMetric extends UpperRunnableMetric
{
    static MainlineMetric of(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new MainlineMetric(startMillis);
    }

    protected MainlineMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
    }
}
