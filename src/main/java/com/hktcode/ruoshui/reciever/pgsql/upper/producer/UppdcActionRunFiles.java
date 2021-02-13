/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleAction;
import com.hktcode.simple.SimpleActionEnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UppdcActionRunFiles extends UppdcActionRun
{
    public static UppdcActionRunFiles of(UppdcConfigFiles config, UppdcMetric metric, UpperHolder holder)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new UppdcActionRunFiles(config, metric, holder);
    }

    private static final Logger logger = LoggerFactory.getLogger(UppdcActionRunFiles.class);

    private UppdcActionRunFiles(UppdcConfigFiles config, UppdcMetric metric, UpperHolder holder)
    {
        super(config, metric, holder);
    }

    @Override
    public SimpleAction<UppdcConfig, UppdcMetric, UpperHolder> next() throws Exception
    {
        final Tqueue<UpperRecordProducer> getout = this.entity.tgtqueue;
        final UppdcConfigFiles config = (UppdcConfigFiles)this.config;
        return SimpleActionEnd.of(this.config, this.metric, this.entity);
    }
}
