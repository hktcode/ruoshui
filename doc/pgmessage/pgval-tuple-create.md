# ```PgsqlTupleCreate```

标识快照开始的消息。

## 属性

字段 | 解释
----|----
```protocol``` | 协议编号，无符号长整型，为固定值```1```
```typename``` | 类型名称，字符串，为固定值```PgsqlTupleCreate```
```relident``` | relation的oid值，长整型
```dbschema``` | relation所在的schema名称，字符串
```relation``` | relation的名称，字符串
```replchar``` | 关系的复制标识，枚举字符串，和```pg_catalog.pg_class```关系中名为```relreplident```属性保持一致。<br/>有以下“复制标识（replica identity）”：<br /><dl><dt>```d```</dt><dd>default (primary key, if any)</dd><br /><dt>```n```</dt><dd>nothing</dd><br/><dt>```f```</dt><dd>all columns</dd><br/><dt>```i```</dt><dd>index with indisreplident set, or default</dd></dl>
```tupleval``` | relation的tuple值列表，PgsqlComponentThe类型的数组，参考下面详细信息

### PgsqlComponenThe

字段 | 解释
----|---
```attrname``` | 属性名称 
```tpschema``` | 属性类型所在的schema
```typename``` | 属性类型名称.
```attflags``` | 标识信息，如果是1，表示是键的一部分，如果是0，表示是普通属性
```datatype``` | 属性类型的oid
```attypmod``` | 属性的```attypmod```信息
```newvalue``` | 元组的值

## 例子：

```json
{ "protocol":1
, "typename":"PgsqlTupleCreate"
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
               , "thevalue":"2"
               }
             , { "attrname":"val"
               , "tpschema":"pg_catalog"
               , "typename":"text"
               , "datatype":25
               , "attypmod":-1
               , "attflags":0
               , "thevalue":"34532"
               }
             ]
}
```
