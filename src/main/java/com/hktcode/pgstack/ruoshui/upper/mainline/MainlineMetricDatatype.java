package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

class MainlineMetricDatatype extends MainlineMetric
{
    static MainlineMetricDatatype of(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new MainlineMetricDatatype(startMillis);
    }

    private MainlineMetricDatatype(ZonedDateTime startMillis)
    {
        super(startMillis);
    }
}
