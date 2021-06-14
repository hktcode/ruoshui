package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

public class UppdcWkstepGaugesFiles extends UppdcWkstepGauges
{
    public static UppdcWkstepGaugesFiles of()
    {
        return new UppdcWkstepGaugesFiles();
    }

    /**
     * 当前打开的文件名.
     */
    public String curFilename = "";

    /**
     * 当前要写入的文件位置，也是当前文件中写入的字节数（含尚未执行fsync的字节数）.
     */
    public long curPosition = 0;

    // - public long fopenMillis = 0; 文件最近打开的时间
    // - public long fsyncMillis = 0; 文件最近刷新的时间
    // - public final CRC32 crc32Buffer = new CRC32(); // 用于计算校验码
    // - public long fsyncLength = 0; 已经刷新的文件长度

    /**
     * 自启动开始写入了多少字节（含尚未执行fsync的字节数）.
     */
    public long totalLength = 0;

    /**
     * 尚未执行fysnc的字节数.
     */
    public long bufferBytes = 0;

    private UppdcWkstepGaugesFiles()
    {
        super();
    }
}
