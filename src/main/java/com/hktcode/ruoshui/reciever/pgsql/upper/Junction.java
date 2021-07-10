package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.queue.Xspins;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalTxactContext;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlKey;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlVal;
import com.hktcode.queue.XArray;
import com.hktcode.queue.XQueue;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Junction extends SimpleWorker
{
    public static Junction of(XQueue<UpperRecordConsumer> recver, XQueue<UpperRecordProducer> sender, Xspins xspins, SimpleAtomic atomic)
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
        return new Junction(recver, sender, xspins, atomic);
    }

    // argval

    public final Xspins xspins;
    public final XQueue<UpperRecordConsumer> recver;
    public final XQueue<UpperRecordProducer> sender;

    // gauges

    public long curlsn = 0;
    public long curseq = 0;
    public final LogicalTxactContext xidenv = LogicalTxactContext.of();

    private Junction(XQueue<UpperRecordConsumer> recver, XQueue<UpperRecordProducer> sender, Xspins xspins, SimpleAtomic atomic)
    {
        super(atomic);
        this.recver = recver;
        this.sender = sender;
        this.xspins = xspins;
    }

    @Override
    protected void run(SimpleAtomic atomic) throws InterruptedException
    {
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        XArray<UpperRecordConsumer> crhs = recver.newArray(), clhs;
        XArray<UpperRecordProducer> plhs = sender.newArray(), prhs;
        long spins = 0;
        long ln, lt = System.currentTimeMillis();
        Iterator<UpperRecordProducer> piter = plhs.iterator();
        Iterator<UpperRecordConsumer> citer = crhs.iterator();
        UpperRecordProducer r = null;
        while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
            long ld = this.xspins.logDuration;
            if (    (plhs.getSize() > 0)
                    // 未来计划：支持bufferCount和maxDuration
                 && (prhs = sender.push(plhs)) != plhs
            ) {
                plhs = prhs;
                spins = 0;
                lt = System.currentTimeMillis();
            } else if (r != null) {
                r = plhs.add(r) ? null : r;
            } else if (piter.hasNext()) {
                r = piter.next();
            } else if (citer.hasNext()) {
                piter = this.convert(citer.next()).iterator();
                lt = System.currentTimeMillis();
            } else if ((clhs = recver.poll(crhs)) != crhs) {
                crhs = clhs;
                citer = crhs.iterator();
            } else if (lt + ld >= (ln = System.currentTimeMillis())) {
                logger.info("logDuration={}", ld);
                lt = ln;
            } else {
                this.xspins.spins(spins++);
            }
        }
        logger.info("upjct complete");
    }

    private List<UpperRecordProducer> convert(UpperRecordConsumer record)
    {
        long lsn = record.lsn;
        LogicalMsg msg = record.msg;

        // 根据对PostgreSQL的测试，作为key的lsn不是严格自增长的.
        // 考虑事务B后于事务A开始，但先于事务A提交。
        // 事务A中写入了a和b两条记录，事务B中写入了c和d两条记录。
        // 事务a、b、c、d写入的顺序为a、c、b、d则：
        // 1、事务B的开始lsn大于事务A的lsn。
        // 2、事务B的XID大于事务A的XID
        // 3、a.lsn > c.lsn > b.lsn > d.lsn。
        // 4、事务B先于事务A提交，事务B先于事务A被接收。
        // 5、事务B的提交lsn小于事务A的提交lsn。
        // 此时LSN不是严格自增长.

        if (msg instanceof LogicalTxactBeginsMsg) {
            this.curlsn = ((LogicalTxactBeginsMsg) msg).lsnofcmt;
            this.curseq = 1;
        }

        ImmutableList<PgsqlVal> vallist = PgsqlVal.of(lsn, msg, this.xidenv);
        List<UpperRecordProducer> result = new ArrayList<>();
        for (PgsqlVal val : vallist) {
            PgsqlKey key = PgsqlKey.of(this.curlsn, this.curseq++, this.xidenv.committs);
            UpperRecordProducer d = UpperRecordProducer.of(key, val);
            result.add(d);
        }
        return ImmutableList.copyOf(result);
    }

    private static final Logger logger = LoggerFactory.getLogger(Junction.class);
}
