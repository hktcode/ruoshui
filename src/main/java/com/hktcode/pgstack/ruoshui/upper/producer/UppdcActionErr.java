/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.triple.kafka.KafkaTripleProducerConfig;
import com.hktcode.lang.exception.ArgumentNullException;

public class UppdcActionErr extends SimpleWorker<UppdcAction> implements UppdcAction
{
    public static UppdcActionErr of(UppdcActionRun action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UppdcActionErr(action, throwsError);
    }

    public static UppdcActionErr of(UppdcActionEnd action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UppdcActionErr(action, throwsError);
    }

    public final UpperProducerConfig config;

    public final UppdcMetricErr metric;

    private UppdcActionErr(UppdcActionEnd action, Throwable throwsError)
    {
        super(action.status, 2);
        this.config = action.config;
        this.metric = UppdcMetricErr.of(action, throwsError);
    }

    private UppdcActionErr(UppdcActionRun action, Throwable throwsError)
    {
        super(action.status, 2);
        this.config = action.config;
        this.metric = UppdcMetricErr.of(action, throwsError);
    }

    @Override
    public UppdcActionErr next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return this;
    }

    @Override
    public UppdcResultErr get()
    {
        return UppdcResultErr.of(this.config, this.metric);
    }
}
