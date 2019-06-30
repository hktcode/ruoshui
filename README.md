# Ruoshui

Ruoshui是一个基于PostgreSQL和Apache Kafka的流式处理框架，在PostgreSQL license协议下开源。
项目名称“Ruoshui”（弱水）取自“任凭弱水三千，我只取一瓢饮”。

该项目还在非常初期的阶段，只完成了从PostgreSQL逻辑复制流中接收消息写入到Kafka中这一基本功能。
就算是已经完成的这一功能，该功能也并不稳定，文档也不完善，本人对代码质量也不满意。
因此如果您在使用中需要帮助，可以从提交记录中找到邮箱地址和我联系。

## 构建

### 要求

* JDK 8
* maven

### 命令

```bash
export RUOSHUI_SRC_HOME=. # the directory for storge the ruoshui code
git clone https://github.com/hktcode/ruoshui.git $RUOSHUI_SRC_HOME
cd $RUOSHUI_SRC_HOME
mvn clean package
```

`$RUOSHUI_SRC_HOME/target/ruoshui-0.0.1-SNAPSHOT-bin.zip`就是我们所需要的文件。

## 运行

### 运行要求

* JDK 8（当前仅在JDK 8下经过测试）
* PostgreSQL 10或者更高版本（当前在PostgreSQL 10.4和11.1中经过测试）

### 命令

```bash
unzip ruoshui-0.0.1-SNAPSHOT-bin.zip 
cd ruoshui-0.0.1-SNAPSHOT
./ruoshui
```
默认启用的端口为8080，如果你需要使用其他端口，可以使用参数--server.port。
例如，假如你想使用8090端口
```bash
./ruoshui --server.port=8090
```

Ruoshui基于Spring Boot开发，```--server.port```是Spring Boot提供的功能。
你也可以其他的Spring Boot设置。

## 使用

Ruoshui项目使用HTTP控制内容，主要的接口有：

### ```PUT api/upper/ruoshui```
添加Ruoshui上游任务，Ruoshui上游任务会从PostgreSQL中接收逻辑复制流消息，并写入到Kafka中。如：
```
PUT http://localhost:8080/api/upper/ruoshui
Content-Type: application/json
Accept: application/json

{ "consumer": { "src_property": {"PGPORT": 5435}
              , "logical_repl": {}
              , "ini_snapshot": {}
              }
, "producer": { "kfk_property": { "bootstrap.servers": "192.168.119.135:9092" }
              , "target_topic": "ruoshui-upper"
              , "partition_no": 0
              }
}
```
以下是JSON串中各个字段的含义：

```
{ "consumer":  // 消费PostgreSQL逻辑复制流消息和产生快照的相关配置。  
  { "src_property": // PostgreSQL的JDBC连接配置
    { "PGPORT": 5432 // PostgreSQL Server的端口信息，默认为5432
    , "PGHOST": "localhost" // PostgreSQL的主机地址，默认为localhost
    , "user": "postgres" // 连接到PostgreSQL的用户名，默认为postgres
    , "password": // 用户名的密码，默认为无
    }
  , "logical_repl": // 逻辑复制流的相关配置 
    { "slot_name": "ruoshui" //  复制槽名称，默认为ruoshui
    , "status_interval": 10000 // 向PostgreSQL主服务器报告复制进度的间隔，用毫秒计算，默认为10000，即10秒。
    , "start_position": 0 // 开始的wal位置，0表示由服务器控制，最好不要使用此参数，默认值为0
    , "options": // 复制槽选项
      { "proto_verion": "1" // 目前只支持1，默认值也是1
      , "publication_names": "ruoshui" // 逗号分隔的publiation名称，默认只为ruoshui.
      }
    }
  , "ini_snapshot": {} // 如果此属性存在，流复制开始前会获取快照，否则不获取。该字段支持快照的相关配置，目前尚未文档化
  }
, "producer": // 写入Kafka者相关配置 
  { "kfk_property":  // Kafka生产者相关配置
    { "bootstrap.servers": "localhost:9092" }
  , "target_topic": "ruoshui-upper" // 要写入的Kafka的Topic，默认值为ruoshui-upper
  , "partition_no": 0 // 要写入的Kafka分区，默认值为0，目前只支持单个分区写入
  }
}
```

注解：
- ```consumer.src_property```的含义可以参考[pgjdbc官方文档](https://jdbc.postgresql.org/documentation/head/connect.html#connection-parameters)。
目前只有```PGHOST```、```PGPORT```、```user```有默认值，其他均没有显式设置（或者说采用Kafka客户端所设置的默认值。
Ruoshui会将```src_property```中的内容变成字符串键值对传递给PostgreSQL的JDBC客户端，因此pgjdbc官方文档中对其的描述均可采用。
- ```consuemr.logical_repl.status_interval```的含义可以参考[PostgreSQL官方文档](https://www.postgresql.org/docs/11/runtime-config-replication.html)中关于```wal_receiver_status_interval```的解释。
- ```consumer.logical_repl.options```目前只支持```proto_verion```和```publication_names```，含义可以参考[PostgreSQL官方文档](https://www.postgresql.org/docs/11/protocol-logical-replication.html)中相关选项的解释。
- ```producer.kfk_property```的含义可以参考[Kafka官方文档](https://kafka.apache.org/11/documentation.html#producerconfigs)。
目前只有```bootstrap.servers```有默认值```localhost:9092```，其他选项均没有显式设置（或者说采用Kafka客户端所设置的默认值）。
Ruoshui会将```kfk_perperty```中的内容变成字符串键值对传递给Kafka生产者，因此Kafka官方文档中对其的描述可以采用。
- 连接到PostgreSQL的用户必须具备复制连接权限，参考PostgreSQL官方文档中关于[pg_hba.conf](https://www.postgresql.org/docs/11/auth-pg-hba-conf.html)的解释。
如果需要获取快照功能，则该用户必须有读取数据库的权限和创建逻辑复制槽的权限，参考PostgreSQL官方文档中[复制协议](https://www.postgresql.org/docs/11/protocol-replication.html)的相关信息。

### ```GET api/upper/ruoshui```

获取Ruoshui任务的信息。例如：

```http request
GET http://localhost:8080/api/upper/ruoshui
Accept: application/json; charset=utf-8
```

目前该请求的返回body尚未稳定，暂时不做介绍。

### ```DELETE api/upper/ruoshui```

删除Ruoshui项目。例如：

```http request
DELETE http://localhost:8080/api/upper/ruoshui
Accept: application/json; charset=utf-8
```
目前该请求的返回body尚未稳定，暂时不做介绍。

### ```POST api/upper/ruoshui/snapshot```

从PostgreSQL中获取一个快照写入Kafka流中。

```
POST http://localhost:8080/api/upper/ruoshui/snapshot
Content-Type: application/json; charset=utf-8

{}
```
目前该请求的body和响应的body尚未稳定，暂时不做介绍。

## 使用例子

* 第一步：先在PostgreSQL中创建好```publication```：
```bash
psql -hlocalhost -Upostgres -c "CREATE PUBLICATION ruoshui FOR ALL TABLES"
```
* 第二步：在PostgreSQL中创建好复制槽（如果你需要初始快照，则不能先创建复制槽），此行为有点奇怪，后续可能更改：
```bash
psql -hlocalhost -Upostgres -c "select pg_create_logical_replication_slot('ruoshui', 'pgoutput')"
```
注意```pg_create_logical_replication_slot```的第二个参数，必须是```pgoutput```。
PostgreSQL官方的逻辑解码插件为```pgoutput```。
* 第三步：启动ruoshui：
```bash
cd $RUOSHUI_HOME
./ruoshui 
```
* 第四步：向ruoshui发送一个HTTP的PUT请求：
  * 如果你需要初始快照，请不要进行第二步，命令如下：
```
PUT http://localhost:8080/api/upper/ruoshui
Content-Type: application/json
Accept: application/json

{ "consumer": { "src_property": {}
              , "logical_repl": {}
              , "ini_snapshot": {}
              }
, "producer": { "kfk_property": { "bootstrap.servers": "localhost:9092" }
              }
}
```
  * 如果你不需要初始快照，则必须进行第二步，命令如下：
```
PUT http://localhost:8080/api/upper/ruoshui
Content-Type: application/json
Accept: application/json

{ "consumer": { "src_property": {}
              , "logical_repl": {}
              }
, "producer": { "kfk_property": { "bootstrap.servers": "localhost:9092" }
              }
}
```
Ruoshui会向名为```ruoshui-upper```（默认）的Topic写入用Json串表示的复制流消息。

## [Ruoshui的JSON串格式](./doc/pgmessage/index.md)
