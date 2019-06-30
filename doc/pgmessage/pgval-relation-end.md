# ```PgsqlRelationEnd```

某个relation快照的开始消息。

## 属性

字段 | 解释
----|----
```protocol``` | 协议编号，无符号长整型，为固定值```9```
```typename``` | 类型名称，字符串，为固定值```PgsqlRelationEnd```
```relident``` | relation的oid值，长整型
```dbschema``` | relation所在的schema名称，字符串
```relation``` | relation的名称，字符串
```replchar``` | 关系的复制标识，枚举字符串，和```pg_catalog.pg_class```关系中名为```relreplident```属性保持一致。<br/>有以下“复制标识（replica identity）”：<br /><dl><dt>```d```</dt><dd>default (primary key, if any)</dd><br /><dt>```n```</dt><dd>nothing</dd><br/><dt>```f```</dt><dd>all columns</dd><br/><dt>```i```</dt><dd>index with indisreplident set, or default</dd></dl>
```attrlist``` | relation的attribute列表，PgsqlAttribute类型的数组，参考[PgvalAttribute的文档](pgval-attribute.md)获取信息

## 例子：

```json
{ "protocol":9
, "typename":"PgsqlRelationEnd"
, "relident":16384
, "dbschema":"public"
, "relation":"ruoshui"
, "replchar":100
, "attrlist":[ {"attrname":"id"
               , "tpschema":"pg_catalog"
               , "typename":"int8"
               , "datatype":20
               , "attypmod":-1
               , "attflags":1
               }
             , { "attrname":"val"
               , "tpschema":"pg_catalog"
               , "typename":"text"
               , "datatype":25
               , "attypmod":-1
               , "attflags":0
               }
             ]
}
```

