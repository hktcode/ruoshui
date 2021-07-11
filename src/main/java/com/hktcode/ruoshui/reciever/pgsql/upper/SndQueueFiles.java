package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.Ruoshui;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
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
import java.util.concurrent.atomic.AtomicLong;

import static java.nio.charset.StandardCharsets.UTF_8;
import static com.hktcode.ruoshui.reciever.pgsql.upper.RhsQueue.Record;

public class SndQueueFiles extends SndQueue
{
    public static final class Schema
    {
        public static final ObjectNode SCHEMA;

        static {
            String filename = "SndQueueFiles.yml";
            SCHEMA = JacksonObject.getFromResource(SndQueue.class, filename);
        }
    }

    public static SndQueueFiles of(JsonNode json, AtomicLong xidlsn)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        SndQueueFiles r = new SndQueueFiles(xidlsn);
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

    private SndQueueFiles(AtomicLong xidlsn)
    {
        super(xidlsn);
        this.innerHandle = new ArrayList<>(1);
    }

    @Override
    public Client client()
    {
        return new Client(this);
    }

    @Override
    public Result toJsonResult()
    {
        return new Result(new Config(this), new Metric(this));
    }

    // handle
    private final List<AsynchronousFileChannel> innerHandle;

    // argval
    private static final Path WALDATA_PATH //
            = Paths.get(Ruoshui.HOME, "var", "data", "revievers.pgsql.upper");

    // - public long senderClass = MAX_SYNCTIME;
    private long maxSynctime = MAX_SYNCTIME;

    private long maxSyncsize = MAX_SYNCSIZE;

    private long maxFilesize = MAX_FILESIZE;

    private long maxFiletime = MAX_FILETIME;

    // gauges
    // - public long fopenMillis = 0; 文件最近打开的时间
    // - public long fsyncMillis = 0; 文件最近刷新的时间
    // - public final CRC32 crc32Buffer = new CRC32(); // 用于计算校验码
    // - public long fsyncLength = 0; 已经刷新的文件长度

    /**
     * 当前打开的文件名.
     */
    private String curFilename = "";

    /**
     * 当前要写入的文件位置，也是当前文件中写入的字节数（含尚未执行fsync的字节数）.
     */
    private long curPosition = 0;

    /**
     * 自启动开始写入了多少字节（含尚未执行fsync的字节数）.
     */
    private long totalLength = 0;

    /**
     * 尚未执行fysnc的字节数.
     */
    private long bufferBytes = 0;

    public static class Client //
            implements SndQueue.Client, CompletionHandler<Integer, Record>
    {
        private final SndQueueFiles sender;

        private Client(SndQueueFiles sender)
        {
            this.sender = sender;
        }

        @Override
        public void send(Record record) throws Throwable
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

        private void fopen(Record record) throws IOException
        {
            OpenOption[] opts = {
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            };
            long t = record.key.timeline;
            long l = record.key.lsnofcmt;
            long s = record.key.sequence;
            String filename = String.format("%08x%016x%016x", t, l, s);
            String curFilename = filename.toUpperCase() + ".jwal";
            Path p = Paths.get(WALDATA_PATH.toString(), curFilename);
            logger.info("fopen : curFilename={}", this.sender.curFilename);
            this.sender.innerHandle.add(AsynchronousFileChannel.open(p, opts));
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
        public void completed(Integer result, Record attachment)
        {
            if (!(attachment.val instanceof PgsqlValTxactCommit)) {
                return;
            }
            PgsqlValTxactCommit val = (PgsqlValTxactCommit)attachment.val;
            this.sender.lastConfirm.set(val.lsnofmsg);
        }

        @Override
        public void failed(Throwable exc, Record attachment)
        {
            logger.error("", exc);
            this.sender.callbackRef.compareAndSet(null, exc);
        }
    }

    public static final class Config extends SndQueue.Config
    {
        public final long maxSynctime;

        public final long maxSyncsize;

        public final long maxFilesize;

        public final long maxFiletime;

        private Config(SndQueueFiles sender)
        {
            super("files");
            this.maxSynctime = sender.maxSynctime;
            this.maxSyncsize = sender.maxSyncsize;
            this.maxFilesize = sender.maxFilesize;
            this.maxFiletime = sender.maxFiletime;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node = super.toJsonObject(node);
            node.put("max_synctime", this.maxSynctime);
            node.put("max_syncsize", this.maxSyncsize);
            node.put("max_filetime", this.maxFiletime);
            node.put("max_filesize", this.maxFilesize);
            return node;
        }
    }

    public static final class Metric extends SndQueue.Metric
    {
        /**
         * 当前打开的文件名.
         */
        public final String curFilename;

        /**
         * 当前要写入的文件位置，也是当前文件中写入的字节数（含尚未执行fsync的字节数）.
         */
        public final long curPosition;

        /**
         * 自启动开始写入了多少字节（含尚未执行fsync的字节数）.
         */
        public final long totalLength;

        /**
         * 尚未执行fysnc的字节数.
         */
        public final long bufferBytes;

        private Metric(SndQueueFiles sender)
        {
            super(sender);
            this.curFilename = sender.curFilename;
            this.curPosition = sender.curPosition;
            this.totalLength = sender.totalLength;
            this.bufferBytes = sender.bufferBytes;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node = super.toJsonObject(node);
            node.put("cur_filename", this.curFilename);
            node.put("cur_position", this.curPosition);
            node.put("total_length", this.totalLength);
            node.put("buffer_bytes", this.bufferBytes);
            return node;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SndQueueFiles.class);
}
