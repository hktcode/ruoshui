# pgkey格式

## 字段和说明

字段 | 说明
-----|----
```timeline``` | 时间线，目前没有使用，为固定值0
```lsnofcmt``` | 无符号长整型，消息的提交LSN，如果是快照消息，则是快照一致点LSN。
```sequence``` | 无符号长整型，在```lsnofcmt```中递增的序列号

因为事务中可能有多条消息，此时，```lsnofcmt```是相同的，所以无法单独通过```lsnofcmt```来判断唯一标识一条消息。但是针对事务和快照，```lsnofcmt```保证不同并且单调递增。

```lsnofcmt```和```sequence```组成针对每条消息单调递增的唯一标识。
可以通过```lsnofcmt```和```sequence```组合的递增在程序中判断是否有重复的消息。

## 例子
```json
{ "timeline": 0
, "lsnofcmt": 232323232
, "sequence": 3232
}
```

## 设计考量

PostgreSQL中接收到的LSN有以下几类：
* 消息的LSN
* 事务开始的LSN
* 事务提交的LSN
* 整个事务最后的LSN
* 快照一致点LSN

其中消息的LSN是按照消息发生的顺序出现的，并不是严格意义的递增，这对实现流式计算的程序并不友好。
事务开始的LSN和消息的LSN有同样的问题，如果事务A在事务B前开始，但是在事务B后提交，则事务A的事务开始LSN小于事务B的开始LSN，但是，我们会先接收到事务B的消息，因此出现了LSN的乱序。
整个事务最后的LSN和事务提交的LSN区别在于，事务提交本身是一个消息，事务提交的LSN是这个提交消息的开始LSN，事务最后的LSN则是这个提交消息的最后一个字节的LSN，理论上来说可以使用整个事务最后的LSN，但是我无法在事务开始时获取该值，事务开始时可以获取提交LSN，处于简单考虑，没有采纳。

快照一致点LSN，则是获取快照时产生的LSN，可以参考PostgreSQL官方文档中关于[```CREATE_REPLICATION_SLOT```](https://www.postgresql.org/docs/11/protocol-replication.html)的描述。
