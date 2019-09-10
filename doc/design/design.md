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

