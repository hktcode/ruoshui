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

    public static UppdcSenderFiles of(UppdcWkstepArgvalFiles argval, UppdcWkstepGaugesFiles gauges)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (gauges == null) {
            throw new ArgumentNullException("gauges");
        }
        return new UppdcSenderFiles(argval, gauges);
    }

    private final UppdcWkstepArgvalFiles argval;

    private final UppdcWkstepGaugesFiles gauges;

    private AsynchronousFileChannel[] handle;

    private UppdcSenderFiles(UppdcWkstepArgvalFiles argval, UppdcWkstepGaugesFiles gauges)
    {
        this.argval = argval;
        this.gauges = gauges;
    }

    @Override
    public void send(UppdcWorkerGauges gauges, UpperRecordProducer record) throws IOException
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
        channel.write(buffer, this.gauges.curPosition, record, new Handler(gauges));
        this.gauges.totalLength += bytes.length;
        this.gauges.curPosition += bytes.length;
        this.gauges.bufferBytes += bytes.length;
        if (this.gauges.curPosition >= this.argval.maxFilesize) {
            this.close();
        }
        else if (this.gauges.bufferBytes >= argval.maxSyncsize) {
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
        this.gauges.curFilename = String.format("%08x%016x%016x.jwal", timeline, lsnofcmt, sequence).toUpperCase();
        Path file = Paths.get(this.argval.walDatapath.toString(), this.gauges.curFilename);
        logger.info("fopen : curFilename={}", this.gauges.curFilename);
        this.handle = new AsynchronousFileChannel[] {
                AsynchronousFileChannel.open(file, options)
        };
        this.gauges.curPosition = 0;
        this.gauges.bufferBytes = 0;
    }

    @Override
    public void close() throws IOException
    {
        for (AsynchronousFileChannel c : this.handle) {
            logger.info("close : curFilename={}", this.gauges.curFilename);
            c.close();
        }
        this.handle = new AsynchronousFileChannel[0];
        this.gauges.curFilename = "";
        this.gauges.bufferBytes = 0;
        this.gauges.curPosition = 0;
    }

    private void fsync() throws IOException
    {
        for (AsynchronousFileChannel c : this.handle) {
            c.force(false);
        }
        this.gauges.bufferBytes = 0;
    }

    private static class Handler implements CompletionHandler<Integer, UpperRecordProducer>
    {
        private final UppdcWorkerGauges gauges;

        public Handler(UppdcWorkerGauges gauges)
        {
            this.gauges = gauges;
        }

        @Override
        public void completed(Integer result, UpperRecordProducer attachment) {
            if (attachment.val instanceof PgsqlValTxactCommit) {
                gauges.txactionLsn.set(((PgsqlValTxactCommit) attachment.val).lsnofmsg);
            }
        }

        @Override
        public void failed(Throwable exc, UpperRecordProducer attachment) {
            gauges.callbackRef.compareAndSet(null, exc);
        }
    }
}
