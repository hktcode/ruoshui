package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThreadBasic;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineThread extends UpperConsumerThreadBasic<MainlineRecord>
{
    public static MainlineThread of
        /* */( MainlineConfig config //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("thread");
        }
        if (status == null) {
            throw new ArgumentNullException("tqueue");
        }
        TransferQueue<MainlineRecord> tqueue = new LinkedTransferQueue<>();
        MainlineSender sender = MainlineSender.of(tqueue, status);
        Thread thread = new Thread(Mainline.of(config, sender));
        thread.start();
        return new MainlineThread(thread, tqueue);
    }

    @Override
    public UpperConsumerRecord //
    poll(long timeout, UpperConsumerMutableMetric metric)
        throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        MainlineRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            return record.update(metric);
        }
        else if (this.thread.isAlive()) {
            return null;
        }
        else {
            // TODO: throw new DelegateNotAliveException();
            throw new RuntimeException();
        }
    }

    protected MainlineThread(Thread thread, TransferQueue<MainlineRecord> tqueue)
    {
        super(thread, tqueue);
    }
}
