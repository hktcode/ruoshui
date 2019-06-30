# ```PgsqlTupleUpdate```

Update消息

## 属性

字段 | 解释
----|----
```protocol``` | 协议编号，无符号长整型，为固定值```7```
```typename``` | 类型名称，字符串，为固定值```PgsqlTupleUpdate```
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
{ "protocol":7
, "typename":"PgsqlTupleUpdate"
, "lsnofmsg":520094176
, "xidofmsg":679
, "committs":1561882526747364
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
               , "newvalue":"3"
               }
             , { "attrname":"val"
               , "tpschema":"pg_catalog"
               , "typename":"text"
               , "datatype":25
               , "attypmod":-1
               , "attflags":0
               , "newvalue":"world"
               }
             ]
}
```
注意，上面这条信息中只有Update语句中的新值，没有旧值，因为我们的UPDATE语句没有改变键（```id```)的值。

```json
{ "protocol":7
, "typename":"PgsqlTupleUpdate"
, "lsnofmsg":536871288
, "xidofmsg":683
, "committs":1561884775847586
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
               , "oldvalue":"3"
               , "newvalue":"4"
               }
             , { "attrname":"val"
               , "tpschema":"pg_catalog"
               , "typename":"text"
               , "datatype":25
               , "attypmod":-1
               , "attflags":0
               , "newvalue":"world"
               }
             ]
}
```
注意，上面这条信息中只有Update语句中键有旧值，因为我们的UPDATE语句改变了键（```id```)的值。

```json
{ "protocol":7
, "typename":"PgsqlTupleUpdate"
, "lsnofmsg":536871968
, "xidofmsg":685
, "committs":1561885090448544
, "relident":16384
, "dbschema":"public"
, "relation":"ruoshui"
, "replchar":102
, "tupleval": [ { "attrname":"id"
                , "tpschema":"pg_catalog"
                , "typename":"int8"
                , "datatype":20
                , "attypmod":-1
                , "attflags":1
                , "oldvalue":"4"
                , "newvalue":"4"
                }
              , { "attrname":"val"
                , "tpschema":"pg_catalog"
                , "typename":"text"
                , "datatype":25
                , "attypmod":-1
                , "attflags":1
                , "oldvalue":"world"
                , "newvalue":"hello"
                }
              ]
}
```
注意，上面这条信息中只有Update语句所有旧值的字段都出现了。

这些不同由PostgreSQL服务端配置，具体请参考```ALTER TABLE [TABLE_NAME] REPLICA IDENTITY FULL;```语句的解释。
