/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactBegins;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleAction;
import com.hktcode.simple.SimpleActionEnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.CRC32;

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
    public SimpleAction<UppdcConfig, UppdcMetric, UpperHolder> next() throws Throwable
    {
        final Tqueue<UpperRecordProducer> getout = this.entity.tgtqueue;
        UpperRecordProducer r = null;
        while (this.entity.run(metric).deletets == Long.MAX_VALUE) {
            if (r == null) {
                r = getout.poll();
            }
            else if (!(r.val instanceof PgsqlValTxactBegins)) {
                r = null;
            }
            else {
                return this.send(r);
            }
        }
        return SimpleActionEnd.of(this.config, this.metric, this.entity);
    }

    private SimpleActionEnd<UppdcConfig, UppdcMetric, UpperHolder> send(UpperRecordProducer begins) //
            throws Throwable
    {
        UpperRecordProducer r = begins;
        do {
            final UppdcConfigFiles config = (UppdcConfigFiles)this.config;
            final OpenOption[] options = {
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE,
            };
            long timeline = r.key.timeline;
            long lsnofcmt = r.key.lsnofcmt;
            long sequence = r.key.sequence;
            String filename = String.format("%08x%016x%016x.jwal", timeline, lsnofcmt, sequence);
            Path directory = Paths.get(config.walDatapath.toString(), this.entity.fullname);
            Files.createDirectories(directory);
            Path file = Paths.get(directory.toString(), filename);
            try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(file, options)) {
                r = this.send(r, channel);
            }
        } while (r != null);
        return SimpleActionEnd.of(this.config, this.metric, this.entity);
    }

    private UpperRecordProducer send(UpperRecordProducer record, AsynchronousFileChannel channel)
            throws Throwable
    {
        CRC32 crc32 = new CRC32();
        final UppdcConfigFiles config = (UppdcConfigFiles)this.config;
        final Tqueue<UpperRecordProducer> getout = this.entity.tgtqueue;
        long byteslen = 0;
        long syncSize = 0;
        byte[] bytes;
        Throwable throwable;
        try {
            do {
                if ((throwable = this.metric.callbackRef.get()) != null) {
                    throw throwable;
                }
                else if (record == null) {
                    record = getout.poll();
                } else if (syncSize > config.maxSyncsize) {
                    channel.force(true);
                    syncSize = 0;
                } else if (byteslen + (bytes = this.toBytesArray(record)).length >= config.maxFilesize && byteslen != 0) {
                    return record;
                } else {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                    channel.write(byteBuffer, byteslen, record, new FileCompletionHandler(metric));
                    crc32.update(bytes);
                    byteslen += bytes.length;
                    syncSize += bytes.length;
                    record = null;
                }
            } while (this.entity.run(metric).deletets == Long.MAX_VALUE);
            return null;
        }
        finally {
            long crc = crc32.getValue();
            bytes = String.format("%016x", crc).getBytes(StandardCharsets.UTF_8);
            channel.write(ByteBuffer.wrap(bytes), byteslen);
        }
    }

    private byte[] toBytesArray(UpperRecordProducer record)
    {
        String keyText = record.key.toJsonObject().toString();
        String valText = record.val.toJsonObject().toString();
        return String.format("%s\t%s\n", keyText, valText).getBytes(StandardCharsets.UTF_8);
    }

    private static class FileCompletionHandler implements CompletionHandler<Integer, UpperRecordProducer>
    {
        private final UppdcMetric metric;

        public FileCompletionHandler(UppdcMetric metric)
        {
            this.metric = metric;
        }

        @Override
        public void completed(Integer result, UpperRecordProducer attachment) {
            if (attachment.val instanceof PgsqlValTxactCommit) {
                metric.txactionLsn.set(((PgsqlValTxactCommit) attachment.val).lsnofmsg);
            }
        }

        @Override
        public void failed(Throwable exc, UpperRecordProducer attachment) {
            metric.callbackRef.compareAndSet(null, exc);
        }
    }
}
