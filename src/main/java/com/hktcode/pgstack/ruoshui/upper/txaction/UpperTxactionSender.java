/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerSender;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperTxactionSender extends UpperConsumerSender<UpperTxactionRecord, UpperTxactionMetric>
{
    public static UpperTxactionSender of //
        /* */( TransferQueue<UpperTxactionRecord> tqueue //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */)
    {
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpperTxactionSender(tqueue, status);
    }

    private UpperTxactionSender //
        /* */( TransferQueue<UpperTxactionRecord> tqueue //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */)
    {
        super(tqueue, status);
    }
}
