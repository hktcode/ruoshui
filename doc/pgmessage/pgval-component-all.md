# ```PgvalComponentAll```

标识关系中的元组信息。

## 属性

字段 | 解释
----|----
```attrname``` | 属性名称 
```tpschema``` | 属性类型所在的schema
```typename``` | 属性类型名称.
```attflags``` | 标识信息，如果是1，表示是键的一部分，如果是0，表示是普通属性
```datatype``` | 属性类型的oid
```attypmod``` | 属性的```attypmod```信息
```oldvalue``` | 属性的旧值，如果没有该属性，表示没有旧值，如[INSERT消息](./pgval-tuple-insert.md)
```newvalue``` | 属性的新值，如果没有该属性，表示没有新值，如[DELETE消息](./pgval-tuple-delete.md)

注意：因为PostgreSQL流复制协议允许服务端只发送[UPDATE消息](./pgval-tuple-update.md)消息中的新值，
或者仅发送旧值中的Key部分，所以[UPDATE消息](./pgval-tuple-update.md)中可能会缺失某些旧值，

## 例子
