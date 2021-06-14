package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class UppdcSenderFiles extends UppdcSender
{
    private static final Logger logger = LoggerFactory.getLogger(UppdcSenderFiles.class);

    public static UppdcSenderFiles of(UppdcWkstepArgvalFiles config, UppdcWkstepGaugesFiles metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UppdcSenderFiles(config, metric);
    }

    private final UppdcWkstepArgvalFiles config;

    private final UppdcWkstepGaugesFiles metric;

    private AsynchronousFileChannel[] handle;

    private UppdcSenderFiles(UppdcWkstepArgvalFiles config, UppdcWkstepGaugesFiles metric)
    {
        this.config = config;
        this.metric = metric;
    }

    @Override
    public void send(UppdcWorkerMeters meters, UpperRecordProducer record) throws IOException
    {
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        String keyText = record.key.toJsonObject().toString();
        String valText = record.val.toJsonObject().toString();
        byte[] bytes = String.format("%s\t%s\n", keyText, valText).getBytes(StandardCharsets.UTF_8);
        if (this.handle.length == 0) {
            this.fopen(record);
        }
        AsynchronousFileChannel channel = this.handle[0];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        channel.write(buffer, metric.curPosition, record, new Handler(meters));
        this.metric.totalLength += bytes.length;
        this.metric.curPosition += bytes.length;
        this.metric.bufferBytes += bytes.length;
        if (this.metric.curPosition >= this.config.maxFilesize) {
            this.close();
        }
        else if (this.metric.bufferBytes >= config.maxSyncsize) {
            this.fsync();
        }
    }

    private void fopen(UpperRecordProducer record) throws IOException
    {
        OpenOption[] options = {
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
        };
        long timeline = record.key.timeline;
        long lsnofcmt = record.key.lsnofcmt;
        long sequence = record.key.sequence;
        this.metric.curFilename = String.format("%08x%016x%016x.jwal", timeline, lsnofcmt, sequence).toUpperCase();
        Path file = Paths.get(this.config.walDatapath.toString(), this.metric.curFilename);
        logger.info("fopen : curFilename={}", this.metric.curFilename);
        this.handle = new AsynchronousFileChannel[] {
                AsynchronousFileChannel.open(file, options)
        };
        this.metric.curPosition = 0;
        this.metric.bufferBytes = 0;
    }

    @Override
    public void close() throws IOException
    {
        for (AsynchronousFileChannel c : this.handle) {
            logger.info("close : curFilename={}", this.metric.curFilename);
            c.close();
        }
        this.handle = new AsynchronousFileChannel[0];
        this.metric.curFilename = "";
        this.metric.bufferBytes = 0;
        this.metric.curPosition = 0;
    }

    private void fsync() throws IOException
    {
        for (AsynchronousFileChannel c : this.handle) {
            c.force(false);
        }
        this.metric.bufferBytes = 0;
    }

    private static class Handler implements CompletionHandler<Integer, UpperRecordProducer>
    {
        private final UppdcWorkerMeters meters;

        public Handler(UppdcWorkerMeters meters)
        {
            this.meters = meters;
        }

        @Override
        public void completed(Integer result, UpperRecordProducer attachment) {
            if (attachment.val instanceof PgsqlValTxactCommit) {
                meters.txactionLsn.set(((PgsqlValTxactCommit) attachment.val).lsnofmsg);
            }
        }

        @Override
        public void failed(Throwable exc, UpperRecordProducer attachment) {
            meters.callbackRef.compareAndSet(null, exc);
        }
    }
}
