# ```PgsqlTupleInsert```

INSERT消息

## 属性

字段 | 解释
----|----
```protocol``` | 协议编号，无符号长整型，为固定值```5```
```typename``` | 类型名称，字符串，为固定值```PgsqlTupleInsert```
```xidofmsg``` | 事务标识（```xid```），无符号长整型
```committs``` | 事务提交时间戳，无符号长整型，为自Unix纪元（```1970-01-01T00:00:00Z```）以来的**微秒数**
```lsnofmsg``` | 消息在WAL中的位置，无符号长整型。
```relident``` | relation的oid值，长整型
```dbschema``` | relation所在的schema名称，字符串
```relation``` | relation的名称，字符串
```replchar``` | 关系的复制标识，枚举字符串，和```pg_catalog.pg_class```关系中名为```relreplident```属性保持一致。<br/>有以下“复制标识（replica identity）”：<br /><dl><dt>```d```</dt><dd>default (primary key, if any)</dd><br /><dt>```n```</dt><dd>nothing</dd><br/><dt>```f```</dt><dd>all columns</dd><br/><dt>```i```</dt><dd>index with indisreplident set, or default</dd></dl>
```tupleval``` | relation的tuple值列表，PgsqlComponentAll类型的数组，参考[PgvalComponentAll的文档](pgval-component-all.md)获取详细信息

## 例子：

```json
{ "protocol":5
, "typename":"PgsqlTupleInsert"
, "lsnofmsg":520093792
, "xidofmsg":678
, "committs":1561882511829161
, "relident":16384
, "dbschema":"public"
, "relation":"ruoshui"
, "replchar":100
, "tupleval":[ { "attrname":"id"
               , "tpschema":"pg_catalog"
               , "typename":"int8"
               , "datatype":20
               , "attypmod":-1
               , "attflags":1
               , "newvalue":"3",
               }
             , { "attrname":"val"
               , "tpschema":"pg_catalog"
               , "typename":"text"
               , "datatype":25
               , "attypmod":-1
               , "attflags":0
               , "newvalue":"hello"
               }
             ]
}
```
