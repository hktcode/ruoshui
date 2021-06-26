package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.Ruoshui;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UppdcSenderFiles extends UppdcSender
{
    public static UppdcSenderFiles of(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        UppdcSenderFiles r = new UppdcSenderFiles();
        r.maxSynctime = json.path("max_synctime").asLong(r.maxSynctime);
        r.maxSyncsize = json.path("max_syncsize").asLong(r.maxSyncsize);
        r.maxFilesize = json.path("max_filesize").asLong(r.maxFilesize);
        r.maxFiletime = json.path("max_filetime").asLong(r.maxFiletime);
        return r;
    }

    public static final long MAX_SYNCTIME = 60000;

    public static final long MAX_SYNCSIZE = 64 * 1024;

    public static final long MAX_FILESIZE = 256 * 1024 * 1024;

    public static final long MAX_FILETIME = 60 * 60 * 1000;

    private UppdcSenderFiles()
    {
        super();
    }

    @Override
    public Client client()
    {
        return new Client(this);
    }

    // handle
    private final List<AsynchronousFileChannel> innerHandle = new ArrayList<>(1);

    // argval
    public static final Path WALDATA_PATH = Paths.get(Ruoshui.HOME, "var", "data", "revievers.pgsql.upper");

    // - public long senderClass = MAX_SYNCTIME;
    public long maxSynctime = MAX_SYNCTIME;

    public long maxSyncsize = MAX_SYNCSIZE;

    public long maxFilesize = MAX_FILESIZE;

    public long maxFiletime = MAX_FILETIME;

    // gauges
    // - public long fopenMillis = 0; 文件最近打开的时间
    // - public long fsyncMillis = 0; 文件最近刷新的时间
    // - public final CRC32 crc32Buffer = new CRC32(); // 用于计算校验码
    // - public long fsyncLength = 0; 已经刷新的文件长度

    /**
     * 当前打开的文件名.
     */
    public String curFilename = "";

    /**
     * 当前要写入的文件位置，也是当前文件中写入的字节数（含尚未执行fsync的字节数）.
     */
    public long curPosition = 0;

    /**
     * 自启动开始写入了多少字节（含尚未执行fsync的字节数）.
     */
    public long totalLength = 0;

    /**
     * 尚未执行fysnc的字节数.
     */
    public long bufferBytes = 0;

    public static class Client implements UppdcSender.Client, CompletionHandler<Integer, UpperRecordProducer>
    {
        private static final Logger logger = LoggerFactory.getLogger(Client.class);

        private final UppdcSenderFiles sender;

        private Client(UppdcSenderFiles sender)
        {
            this.sender = sender;
        }

        @Override
        public void send(UpperRecordProducer record) throws Throwable
        {
            if (record == null) {
                throw new ArgumentNullException("record");
            }
            Throwable ex = this.sender.callbackRef.get();
            if (ex != null) {
                throw ex;
            }
            String key = record.key.toJsonObject().toString();
            String val = record.val.toJsonObject().toString();
            byte[] bytes = String.format("%s\t%s\n", key, val).getBytes(UTF_8);
            if (this.sender.innerHandle.size() == 0) {
                this.fopen(record);
            }
            AsynchronousFileChannel channel = this.sender.innerHandle.get(0);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            channel.write(buffer, this.sender.curPosition, record, this);
            this.sender.totalLength += bytes.length;
            this.sender.curPosition += bytes.length;
            this.sender.bufferBytes += bytes.length;
            if (this.sender.curPosition >= this.sender.maxFilesize) {
                this.close();
            }
            else if (this.sender.bufferBytes >= this.sender.maxSyncsize) {
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
            String curFilename = String.format("%08x%016x%016x.jwal", timeline, lsnofcmt, sequence).toUpperCase();
            Path file = Paths.get(WALDATA_PATH.toString(), curFilename);
            logger.info("fopen : curFilename={}", this.sender.curFilename);
            this.sender.innerHandle.add(AsynchronousFileChannel.open(file, options));
            this.sender.curPosition = 0;
            this.sender.bufferBytes = 0;
            this.sender.curFilename = curFilename;
        }

        @Override
        public void close() throws IOException
        {
            for (AsynchronousFileChannel c : this.sender.innerHandle) {
                logger.info("close : curFilename={}", this.sender.curFilename);
                c.close();
            }
            this.sender.innerHandle.clear();
            this.sender.curFilename = "";
            this.sender.bufferBytes = 0;
            this.sender.curPosition = 0;
        }

        private void fsync() throws IOException
        {
            for (AsynchronousFileChannel c : this.sender.innerHandle) {
                c.force(false);
            }
            this.sender.bufferBytes = 0;
        }

        @Override
        public void completed(Integer result, UpperRecordProducer attachment)
        {
            if (!(attachment.val instanceof PgsqlValTxactCommit)) {
                return;
            }
            PgsqlValTxactCommit val = (PgsqlValTxactCommit)attachment.val;
            this.sender.txactionLsn.set(val.lsnofmsg);
        }

        @Override
        public void failed(Throwable exc, UpperRecordProducer attachment)
        {
            logger.error("", exc);
            this.sender.callbackRef.compareAndSet(null, exc);
        }
    }
}
