/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.triple.TripleActionErr;
import com.hktcode.bgsimple.triple.TripleMetricErr;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmActionErr extends TripleActionErr<UpcsmAction, UpcsmConfig, UpcsmMetricRun>
    implements UpcsmAction
{
    public static UpcsmActionErr of(UpcsmActionRun action, Throwable throwsError) throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        UpcsmMetricRun basicMetric;
        if (throwsError instanceof FetchThreadThrowsErrorException) {
            UpcsmReportSender report = ((FetchThreadThrowsErrorException) throwsError).sender;
            basicMetric = UpcsmMetricRun.of(action, report);
        }
        else {
            basicMetric = action.get().metric;
        }
        TripleMetricErr<UpcsmMetricRun> metric = TripleMetricErr.of(basicMetric, throwsError);
        return new UpcsmActionErr(action, metric);
    }

    private UpcsmActionErr(UpcsmActionRun action, TripleMetricErr<UpcsmMetricRun> metric)
    {
        super(action, action.config, metric, 0);

    }
}
