/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.datatype;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerConfig;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.txaction.UpperTxactionConfig;
import com.hktcode.pgstack.ruoshui.upper.txaction.UpperTxactionThreadInit;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.atomic.AtomicReference;

public class UpperDatatypeRecordFinish implements UpperDatatypeRecord
{
    public static UpperDatatypeRecordFinish of
        /* */( UpperConsumerConfig config
        /* */, PgConnection pgrepl
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpperDatatypeRecordFinish(config, pgrepl, status);
    }

    private final UpperConsumerConfig config;

    private final PgConnection pgrepl;

    private final AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status;

    public UpperConsumerRecord update(UpperConsumerMutableMetric metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        UpperTxactionConfig c = UpperTxactionConfig.of(config.logicalSlot);
        c.waitTimeout = config.waitTimeout;
        c.logDuration = config.logDuration;
        metric.fetchThread = UpperTxactionThreadInit.of(c, pgrepl, status);
        return null;
    }

    private UpperDatatypeRecordFinish
        /* */( UpperConsumerConfig config
        /* */, PgConnection pgrepl
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */)
    {
        this.config = config;
        this.pgrepl = pgrepl;
        this.status = status;
    }
}
