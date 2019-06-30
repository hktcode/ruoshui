# PgsqlNoOperation

没有任何信息的消息类型，添加本类型只是为了完整性，你不可能从Kafka中接收到此类型。
不过，如果你和我一样讨厌```null```，当Kafka中没有消息但是要返回给客户端信息时，你可以使用此类型代替```null```。
生产者保证不会往Kafka中写入此类型

## 属性

字段 | 解释 
---|---
protocol | 协议编号，为固定值0
typename | 类型名称，为固定值```PgsqlNoOperation```

## 例子

```json
{ "protocol": 0, "typename": "PgsqlNoOperation" }
```
