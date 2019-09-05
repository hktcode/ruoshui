/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.triple.kafka.KafkaTripleProducerConfig;
import com.hktcode.lang.exception.ArgumentNullException;

public class UppdcActionEnd extends SimpleWorker<UppdcAction> implements UppdcAction
{
    public static UppdcActionEnd of(UppdcActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UppdcActionEnd(action);
    }

    public final UpperProducerConfig config;

    public final UppdcMetricEnd metric;

    private UppdcActionEnd(UppdcActionRun action)
    {
        super(action.status, 2);
        this.config = action.config;
        this.metric = UppdcMetricEnd.of(action);
    }

    @Override
    public UppdcActionErr next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return UppdcActionErr.of(this, throwsError);
    }

    @Override
    public UppdcResultEnd get()
    {
        return UppdcResultEnd.of(this.config, this.metric);
    }
}
