# ```PgsqlTxactBegins```

事务开始消息。

## 属性

字段 | 解释
----|----
```protocol``` | 协议编号，无符号长整型，为固定值```2```
```typename``` | 类型名称，字符串，为固定值```PgsqlTxactBegins```
```xidofmsg``` | 事务标识（```xid```），无符号长整型
```committs``` | 事务提交时间戳，无符号长整型，为自Unix纪元（```1970-01-01T00:00:00Z```）以来的**微秒数**
```lsnofmsg``` | 消息在WAL中的位置，无符号长整型。

## 例子

```json
{ "protocol":2
, "typename":"PgsqlTxactBegins"
, "lsnofmsg":520094360
, "xidofmsg":680
, "committs":1561882549660828
}
```

