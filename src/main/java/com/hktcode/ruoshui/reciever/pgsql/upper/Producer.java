package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xspins;
import com.hktcode.queue.XArray;
import com.hktcode.queue.XQueue;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class Producer extends SimpleWorker
{
    public static Producer of(XQueue<UpperRecordProducer> recver, SndQueue sender, SimpleAtomic atomic)
    {
        if (recver == null) {
            throw new ArgumentNullException("recver");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        return new Producer(recver, sender, atomic);
    }

    public final SndQueue sender;

    public final XQueue<UpperRecordProducer> recver;

    public final Xspins xspins = Xspins.of();

    private Producer(XQueue<UpperRecordProducer> recver, SndQueue sender, SimpleAtomic atomic)
    {
        super(atomic);
        this.sender = sender;
        this.recver = recver;
    }

    @Override
    public void run(SimpleAtomic atomic) throws Throwable
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        XArray<UpperRecordProducer> lhs, rhs = recver.newArray();
        long now, prelog = System.currentTimeMillis(), spins = 0;
        Iterator<UpperRecordProducer> iter = rhs.iterator();
        try (SndQueue.Client client = this.sender.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                long l = this.xspins.logDuration;
                if (iter.hasNext()) {
                    // 未来计划：send方法支持数组，发送多个记录，提高性能
                    client.send(iter.next());
                } else if ((lhs = recver.poll(rhs)) != rhs) {
                    rhs = lhs;
                    iter = rhs.iterator();
                } else if (prelog + l >= (now = System.currentTimeMillis())) {
                    logger.info("write to logDuration={}", l);
                    prelog = now;
                } else {
                    this.xspins.spins(spins++);
                }
            }
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
}
