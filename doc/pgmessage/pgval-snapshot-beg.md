# ```PgsqlSnapshotBeg```

标识快照开始的消息。

## 属性

字段 | 解释
----|----
```protocol``` | 协议编号，无符号长整型，为固定值```10```
```typename``` | 类型名称，字符串，为固定值```PgsqlSnapshotBeg```

## 例子：

```json
{ "protocol":10
, "typename":"PgsqlSnapshotBeg",
}
```
