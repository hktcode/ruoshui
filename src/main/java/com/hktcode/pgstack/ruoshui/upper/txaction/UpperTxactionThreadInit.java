/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperTxactionThreadInit extends UpperTxactionThread
{
    public static UpperTxactionThreadInit of
        /* */( UpperTxactionConfig config //
        /* */, PgConnection pgrepl //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
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
        TransferQueue<UpperTxactionRecord> tqueue = new LinkedTransferQueue<>();
        UpperTxactionSender sender = UpperTxactionSender.of(tqueue, status);
        Thread thread = new Thread(UpperTxaction.of(config, pgrepl, sender));
        thread.start();
        return new UpperTxactionThreadInit(thread, tqueue);
    }

    private UpperTxactionThreadInit(Thread thread, TransferQueue<UpperTxactionRecord> tqueue)
    {
        super(thread, tqueue);
    }
}
