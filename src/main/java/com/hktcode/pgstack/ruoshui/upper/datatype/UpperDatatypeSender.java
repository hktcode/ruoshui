/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.datatype;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerSender;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperDatatypeSender extends UpperConsumerSender<UpperDatatypeRecord, UpperDatatypeMetric>
{
    public static UpperDatatypeSender of //
        /* */( TransferQueue<UpperDatatypeRecord> tqueue //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */)
    {
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpperDatatypeSender(tqueue, status);
    }

    private UpperDatatypeSender //
        /* */( TransferQueue<UpperDatatypeRecord> tqueue //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */)
    {
        super(tqueue, status);
    }
}
