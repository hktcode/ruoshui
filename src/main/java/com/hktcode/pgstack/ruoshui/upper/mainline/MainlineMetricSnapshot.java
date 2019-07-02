package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

class MainlineMetricSnapshot extends MainlineMetric
{
    static MainlineMetricSnapshot of(ZonedDateTime startMillis)
    {
        if (null == startMillis) {
            throw new ArgumentNullException("startMillis");
        }
        return new MainlineMetricSnapshot(startMillis);
    }

    private MainlineMetricSnapshot(ZonedDateTime startMillis)
    {
        super(startMillis);
    }
}
