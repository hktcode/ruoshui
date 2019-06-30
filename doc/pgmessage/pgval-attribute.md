# ```PgvalAttribute```

表示relation中的一个属性。

## 属性

字段 | 解释
----|----
```attrname``` | 属性名称 
```tpschema``` | 属性类型所在的schema
```typename``` | 属性类型名称.
```attflags``` | 标识信息，如果是1，表示是键的一部分，如果是0，表示是普通属性
```datatype``` | 属性类型的oid
```attypmod``` | 属性的```attypmod```信息

## 例子


