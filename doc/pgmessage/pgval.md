# PgVal消息体

## 字段和说明

PgVal有很多种类型，所有的类型都有typename属性和protocol属性，typename属性是一个字符串，用于表示具体的类型信息。
而protocol则是一个数字，用于表示该类型的版本。
考虑INSERT消息，它的typename是PgsqlTupleInsert，protocol是5，如果后续版本升级，需要在INSERT消息中添加属性，
那么typename还是PgsqlTupleInsert，但是protocol就是比5大的数字。
注意protocol针对某个类型的消息版本变更不一定是递增的。
采用typename和protocol的组合标识一个类型，但同时让protocol在整个类型中唯一，这是笔者对Avro格式的研究中获得的成果之一。
将来的某一天Avro格式会称为本项目首要支持的标准格式。

typename | protocol | 描述
:--------|---------:|:-------
[```PgsqlNoOperation```](pgval-no-operation.md) |  0 | 添加本类型只是为了完整性，你不可能接收到此类型消息
[```PgsqlSnapshotBeg```](pgval-snapshot-beg.md) | 10 | 整个快照的开始消息
[```PgsqlRelationBeg```](pgval-relation-beg.md) |  8 | 单个relation快照的开始消息
[```PgsqlTupleCreate```](pgval-tuple-create.md) |  1 | 在快照中的tuple消息
[```PgsqlRelationEnd```](pgval-relation-end.md) |  9 | 单个relation快照的结束消息
[```PgsqlSnapshotEnd```](pgval-snapshot-end.md) | 11 | 整个快照的结束消息
[```PgsqlTxactBegins```](pgval-txact-begins.md) |  2 | 事务开始消息
[```PgsqlTruncateRel```](pgval-truncate-rel.md) |  4 | truncate整个relation消息
[```PgsqlTupleInsert```](pgval-tuple-insert.md) |  5 | insert消息
[```PgsqlTupleUpdate```](pgval-tuple-update.md) |  7 | update消息
[```PgsqlTupleDelete```](pgval-tuple-delete.md) |  6 | delete消息
[```PgsqlTxactCommit```](pgval-txact-commit.md) |  3 | 事务提交消息

在内部代码上，标识PgsqlVal的代码为类```com.hktcode.pgstack.ruoshui.pgsql.PgsqlVal```，具体的类型都有前缀```PgsqlVal```。

