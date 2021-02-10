# Ruoshui

Ruoshui是一个基于PostgreSQL和Apache Kafka的流式处理框架，在PostgreSQL license协议下开源，
采用Spring Boot开发，对外提供RESTful风格的接口。
项目名称“Ruoshui”（弱水）取自“任凭弱水三千，我只取一瓢饮”。

该项目还在非常初期的阶段，只完成了从PostgreSQL逻辑复制流中接收消息写入到Kafka中这一基本功能。
就算是已经完成的这一功能，该功能也不稳定，文档也不完善，本人对代码质量也不满意。

如果在使用中需要帮助，可以从提交记录中找到邮箱地址和我联系。

## [构建运行](/doc/01.build.md)

## [快速开始](/doc/02.quick-start.md)

## [用户手册](/doc/03.user-manual.md)

## [消息格式](/doc/04.message.md)

## [设计理念](./doc/design/design.md)

* support ```Accept: application/xml``` request
* hight available by zookeeper
  * waiting
  * working
  * primary
  * standby
  * onlyone

* ~优化UpperSender.poll~ 2019年9月12日
* ruoshui-reciever
    * ruoshui-pgsql
    * ruoshui-mysql
    * 接收wal（upper）
    * 去重wal（lower）
* ruoshui-standard
    * 转换标准化wal
* ruoshui-persists
    * 写入hbase
    * hbase写入hive
    * hive视图
* ruoshui-snapshot
    * 定时快照
* ruoshui-checking
    * 计算快照
    * 定时比较验证
* ruoshui-reversal
    * 规范化wal中实现冲正
    * 去重wal
    
* ladle花洒(sprinkler)
* 通用计算逻辑

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
