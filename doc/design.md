* support ```Accept: application/xml``` request
* hight available by zookeeper
    * waiting
    * working
    * primary
    * standby
    * onlyone

* ~优化UpperSender.poll~ 2019年9月12日
* ruoshui-reciever
    * ruoshui-reciever-pgsql
        * 接收wal（upper）
        * 去重wal（among）
        * 规范wal（lower）
    * ruoshui-reciever-mysql
        * 接收wal（upper）
        * 去重wal（among）
        * 规范wal（lower）
* ruoshui-snapshot
    * ruoshui-snapshot-pgsql
        * 定时快照（reset）
    * ruoshui-snapshot-mysql
        * 定时快照（reset）
* ruoshui-persists
    * 写入hbase（Kafka消费者）(hbive.upper)
    * hive交互(hbive.among)
        * hbase写入hive（定时）
        * hive视图（定时）
    * 计算快照（定时）(hbive.lower)
* ruoshui-reversal
    * 比较验证（定时）(check)
        * 规范化wal中实现冲正（定时）
* ruoshui-endpoint
    * 花洒(sprinkler)(ladle)(streams)
    * 通用计算逻辑(drink)

# Ruoshui设计

Ruoshui分成五大模块，分别是UPPER（上游）、AMONG（中游）、LOWER（下游）、LADLE（一瓢）和DRINK（饮）。其各个功能如下：
1. UPPER（上游）项目从PostgreSQL中接收流复制消息和产生快照消息流，写入到Kafka中。
2. AMONG（中游）项目对UPPER（上游）的消息进行去重，确保生产者生产且仅生产一次。
3. LOWER（下游）项目将AMONG（中游）的消息进行转换，转换成各个数据库通用的格式。
4. LADLE（一瓢）项目消费Kafka消息，分发到不同的topic中。
5. DRINK（饮用）项目消费Kafka消息，进行计算，写入外部存储中。

当前项目开发仅仅开发了UPPER（上游）模块。

根据分布式理论，从PostgreSQL中接收消息写入到Kafka中可能会出现重复生产消息的情况。
将UPPER（上游）项目中的消息进行去重，这是AMONG（中游）项目存在的意义之一。
这也是UPPER（上游）项目只支持向只有一个PARTITION的TOPIC写入消息的原因。
因为需要去重，如果写入了多个PARTITION（可能这些PARTITION不再同一个TOPIC中），则需要对每个PARTITION去重，增加了工作量。

AMONG（中游）项目、LOWER（下游）和LADLE（一瓢）都采用Kafka事务机制，确保消息的一次且仅一次消费。
和当前市面上直接向多个PARTITION写入消息的组件不同，Ruoshui项目采用的是分发机制。
LADLE（一瓢）项目负责分发，这样子的好处在于，分发和接收消息相分离，分发程序可以不同考虑生产者重复生产消息的情况。

DRINK（饮用）项目是最后的终点。
该项目的难点在于，如果外部存储不支持事务机制，如何保证一次且仅一次消费。

暂时不实现修改配置

* 配置文件
    * 数据文件
        * 恢复脚本
* 单元测试
* 界面设计
* 数据展示
* mysql支持

* 高可用
* 数据去重
    * 数据规范
        * 计算快照
* 查询快照
    * 冲正
* 花洒
    * drink

Throwable (java.lang)
Exception (java.lang)
IOException (java.io)
InterruptedIOException (java.io)
EOFException (java.io)
FileNotFoundException (java.io)
UnsupportedEncodingException (java.io)
SyncFailedException (java.io)
ClosedChannelException (java.nio.channels)
AsynchronousCloseException (java.nio.channels)
InterruptedByTimeoutException (java.nio.channels)
UserPrincipalNotFoundException (java.nio.file.attribute)
CharacterCodingException (java.nio.charset)
MalformedInputException (java.nio.charset)
UnmappableCharacterException (java.nio.charset)
FileLockInterruptionException (java.nio.channels)
FileSystemException (java.nio.file)
NoSuchFileException (java.nio.file)
FileSystemLoopException (java.nio.file)
NotDirectoryException (java.nio.file)
DirectoryNotEmptyException (java.nio.file)
NotLinkException (java.nio.file)
AtomicMoveNotSupportedException (java.nio.file)
FileAlreadyExistsException (java.nio.file)
AccessDeniedException (java.nio.file)

exclass
errcode
advises
doclink
message

description
suggestions
messagecode
exclassname
helplinkdoc
issueweburl

配置文件存储

etc/
+ init/
    + recievers/
        + name.upper.yml
+ auto/
    + recievers/
        + name.upper.yml
          var/
+ logs/
    + allsystem/
    + recievers/
+ data/
    + recievers/
+ lock/
    + allsystem/
    + recievers/
+ runs/
    + recievers/
+ http/
    + recievers/

init - run - stop/pause/suspend - end/finish/complete/terminate