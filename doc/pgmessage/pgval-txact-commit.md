# ```PgsqlTxactCommit```

事务提交消息。

## 属性

字段 | 解释
----|----
```protocol``` | 协议编号，无符号长整型，为固定值```3```
```typename``` | 类型名称，字符串，为固定值```PgsqlTxactCommit```
```xidofmsg``` | 事务标识（```xid```），无符号长整型
```committs``` | 事务提交时间戳，无符号长整型，为自Unix纪元（```1970-01-01T00:00:00Z```）以来的**微秒数**
```lsnofmsg``` | 消息在WAL中的位置，无符号长整型。
```xidflags``` | 事务提交标记，无符号长整型。

## 例子

```json
{ "protocol":3
, "typename":"PgsqlTxactCommit"
, "lsnofmsg":520094480
, "xidofmsg":680
, "committs":1561882549660828
, "xidflags":0
}
```