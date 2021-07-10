package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xspins;
import com.hktcode.queue.XArray;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Consumer extends SimpleWorker
{
    public static Consumer of(RcvQueue recver, LhsQueue sender, Xspins xspins, SimpleAtomic atomic)
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
        return new Consumer(recver, sender, xspins, atomic);
    }

    public final Xspins xspins;

    public final LhsQueue sender;

    public final RcvQueue recver;

    @Override
    protected void run(SimpleAtomic atomic) throws SQLException, InterruptedException
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        LhsQueue.Record r = null;
        XArray<LhsQueue.Record> rhs, lhs = this.sender.newArray();
        int spins = 0, spinsStatus = Xspins.RESET;
        long now, logtime = System.currentTimeMillis();
        try (RcvQueue.Client client = this.recver.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                // 未来计划：此处可以提高性能
                long logDuration = this.xspins.logDuration;
                if (lhs.getSize() > 0 && (rhs = sender.push(lhs)) != lhs) {
                    lhs = rhs;
                    spins = 0;
                    logtime = System.currentTimeMillis();
                } else if (r == null) {
                    r = client.recv();
                } else if (lhs.add(r)) {
                    spins = 0;
                    logtime = System.currentTimeMillis();
                } else if (logtime + logDuration >= (now = System.currentTimeMillis())) {
                    logger.info("logDuration={}", logDuration);
                    logtime = now;
                } else {
                    if (spinsStatus == Xspins.SLEEP) {
                        client.forceUpdateStatus();
                    }
                    spinsStatus = this.xspins.spins(spins++);
                }
            }
        }
        logger.info("pgsender complete");
    }

    private Consumer(RcvQueue recver, LhsQueue sender, Xspins xspins, SimpleAtomic atomic)
    {
        super(atomic);
        this.sender = sender;
        this.recver = recver;
        this.xspins = xspins;
    }

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
}
