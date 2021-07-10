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
    public static Producer of(RhsQueue recver, SndQueue sender, Xspins xspins, SimpleAtomic atomic)
    {
        if (recver == null) {
            throw new ArgumentNullException("recver");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        if (xspins == null) {
            throw new ArgumentNullException("xspins");
        }
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        return new Producer(recver, sender, xspins, atomic);
    }

    public final SndQueue sender;

    public final RhsQueue recver;

    public final Xspins xspins;

    private Producer(RhsQueue recver, SndQueue sender, Xspins xspins, SimpleAtomic atomic)
    {
        super(atomic);
        this.sender = sender;
        this.recver = recver;
        this.xspins = xspins;
    }

    @Override
    public void run(SimpleAtomic atomic) throws Throwable
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        XArray<RhsQueue.Record> lhs, rhs = recver.newArray();
        long now, prelog = System.currentTimeMillis(), spins = 0;
        Iterator<RhsQueue.Record> iter = rhs.iterator();
        try (SndQueue.Client client = this.sender.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                long l = this.xspins.logDuration;
                if (iter.hasNext()) {
                    // 未来计划：send方法支持数组，发送多个记录，提高性能
                    client.send(iter.next());
                } else if ((lhs = recver.poll(rhs)) != rhs) {
                    iter = (rhs = lhs).iterator();
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
