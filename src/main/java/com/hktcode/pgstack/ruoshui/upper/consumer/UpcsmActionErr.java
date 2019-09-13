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
        UpcsmReportSender report;
        if (throwsError instanceof FetchThreadThrowsErrorException) {
            report = ((FetchThreadThrowsErrorException) throwsError).sender;
        }
        else {
            report = action.fetchThread.get();
        }
        basicMetric = UpcsmMetricRun.of(action, report);
        TripleMetricErr<UpcsmMetricRun> metric = TripleMetricErr.of(basicMetric, throwsError);
        return new UpcsmActionErr(action, metric);
    }

    private UpcsmActionErr(UpcsmActionRun action, TripleMetricErr<UpcsmMetricRun> metric)
    {
        super(action, action.config, metric, 0);

    }
}
