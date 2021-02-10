# PgVal消息体

## 字段和说明

PgVal有很多种类型，所有的类型都有typename属性和protocol属性，typename属性是一个字符串，用于表示具体的类型信息。
而protocol则是一个数字，用于表示该类型的版本。
考虑INSERT消息，它的typename是PgsqlTupleInsert，protocol是5，如果后续版本升级，需要在INSERT消息中添加属性，
那么typename还是PgsqlTupleInsert，但是protocol就是比5大的数字。
注意protocol针对某个类型的消息版本变更不一定是递增的。
采用typename和protocol的组合标识一个类型，但同时让protocol在整个类型中唯一，这是笔者对Avro格式的研究中获得的成果之一。
将来的某一天Avro格式会称为本项目首要支持的标准格式。

typename | protocol | 描述
:--------|---------:|:-------
[```PgsqlNoOperation```](pgval-no-operation.md) |  0 | 添加本类型只是为了完整性，你不可能接收到此类型消息
[```PgsqlHeartBeatss```]()                      |    | 心跳消息

[```PgsqlTxactBegins```](pgval-txact-begins.md) |  2 | 事务开始消息
[```PgsqlTupleInsert```](pgval-tuple-insert.md) |  5 | insert消息
[```PgsqlTupleUpdate```](pgval-tuple-update.md) |  7 | update消息
[```PgsqlTupleUpdert```](pgval-tuple-update.md) |  7 | update消息
[```PgsqlTupleUpdete```](pgval-tuple-update.md) |  7 | update消息
[```PgsqlTupleDelete```](pgval-tuple-delete.md) |  6 | delete消息
[```PgsqlTxactCommit```](pgval-txact-commit.md) |  3 | 事务提交消息

[```PgsqlSnapshotBeg```](pgval-snapshot-beg.md) | 10 | 整个快照的开始消息
[```PgsqlRelationBeg```](pgval-relation-beg.md) |  8 | 单个relation快照的开始消息
[```PgsqlTupleExists```](pgval-tuple-create.md) |  1 | 在快照中的tuple消息
[```PgsqlTupleExiert```](pgval-tuple-create.md) |  1 | 在快照中的tuple消息
[```PgsqlTupleExiete```](pgval-tuple-create.md) |  1 | 在快照中的tuple消息
[```PgsqlRelationEnd```](pgval-relation-end.md) |  9 | 单个relation快照的结束消息
[```PgsqlSnapshotEnd```](pgval-snapshot-end.md) | 11 | 整个快照的结束消息

[```PgsqlAlterrelRel```](pgval-alterrel-beg.md) |    | 表结构修改开始消息
[```PgsqlAlterrelBeg```](pgval-alterrel-beg.md) |    | 表结构修改开始消息
[```PgsqlTupleModify```](pgval-tuple-modify.md) |    | 表结构修改导致tuple变更消息
[```PgsqlTupleModert```](pgval-tuple-modify.md) |    | 表结构修改导致tuple变更消息
[```PgsqlTupleModete```](pgval-tuple-modify.md) |    | 表结构修改导致tuple变更消息
[```PgsqlAlterrelEnd```](pgval-alterrel-end.md) |    | 表结构修改结束消息

[```PgsqlDroprelaRel```](pgval-alterrel-beg.md) |    | DROP表开始消息
[```PgsqlDroprelaBeg```](pgval-alterrel-beg.md) |    | DROP表开始消息
[```PgsqlTupleRemove```](pgval-tuple-modify.md) |    | DROP表修改导致tuple丢失消息
[```PgsqlDroprelaEnd```](pgval-alterrel-end.md) |    | DROP表结束消息

[```PgsqlCreatereRel```](pgval-truncate-rel.md) |    | create relation消息
[```PgsqlCreatereBeg```](pgval-alterrel-beg.md) |    | create表开始消息
[```PgsqlTupleAppend```](pgval-tuple-append.md) |    | create表时添加tuple消息
[```PgsqlCreatereEnd```](pgval-alterrel-end.md) |    | create表结束消息

[```PgsqlTruncateRel```](pgval-truncate-rel.md) |  4 | truncate整个relation消息
[```PgsqlTruncateBeg```](pgval-alterrel-beg.md) |    | Truncate表开始消息
[```PgsqlTupleErased```](pgval-tuple-append.md) |    | Truncate表时丢失的tuple消息
[```PgsqlTruncateEnd```](pgval-alterrel-end.md) |    | Truncate表结束消息

在内部代码上，标识PgsqlVal的代码为类```com.hktcode.pgstack.ruoshui.pgsql.PgsqlVal```，具体的类型都有前缀```PgsqlVal```。


- IU, DU  update
- IN, DN  normal
- IA, DA  alter
- IS, DS  snapshot
- IR  create
- DD  drop
- DT  truncate

- BX, CX transaction
- BU, CU update
- BC, CC create table
- BA, CA alter table
- BD, CD drop table
- BT, CT truncate
- BS, CS snapshot
- BR, CR table snapshot

Append, remove
          类别           | 单条记录 | 开始记录 | 结束记录 | 新增元组 | 删除元组 | 修改元组 | 改前元组 | 改后元组 | 元组记录 | 之前删除 | 之前新增 |
                         |  single  |  begins  |  finish  |  insert  |  delete  |  update  |  updete  |  updert  |  record  |  erased  |  supply  |
-------------------------|----------|----------|----------|----------|----------|----------|----------|----------|----------|----------|----------|
新建关系(CreateRelation) |   SC     |   BC     |    CC    |   IC     |   无     |    无    |   无     |   无     |    无    |    PC    |    QC    |
删除关系(DeleteRelation) |   SD     |   BD     |    CD    |   无     |   DD     |    无    |   无     |   无     |    无    |    PD    |    QD    |
修改关系(UpdateRelation) |   SA     |   BA     |    CA    |   无     |   无     |    UA    |   TA     |   VA     |    无    |    PA    |    QA    |
清空关系(ErasedRelation) |   ST     |   BT     |    CT    |   无     |   DT     |    无    |   无     |   无     |    无    |    PT    |    QT    |
关系快照(SelectRelation) |   无     |   BR     |    CR    |   无     |   无     |    无    |   无     |   无     |    RR    |    PR    |    QR    |
普通事务(NormalTxaction) |   无     |   BX     |    CX    |   IX     |   DX     |    UX    |   TX     |   VX     |    无    |    无    |    无    |
快照信息(SelectSnapshot) |   无     |   BS     |    CS    |   无     |   无     |    无    |   无     |   无     |    无    |    无    |    无    |
        (EmptyOperation) 
        (HeartbeatInfor) 
        
规范化：
        类别         |  开始记录 | 结束记录 | 
                     |  begins0  | finish1  |
---------------------|-----------|----------|
元组变动(Tupleval)0  |     BN    |    FN    |
关系变动(Relation)1  |     BC    |    FC    |

事务(Txaction) |    BX    |    CX    |
冲正(Reversal) |    BS    |    CS    |
        
1. 上下文
2. 新增或删除（-1, 0, 1）
3. 是否冲正Tupleval（-1, 0, 1）
4. 此动作之后表是否存在（-1, 0, 1）
5. 冲正表结构
6. 主键变更的处理
7. 不区分新增元组和删除，修改为采用字段表示

流式数据表。
快照数据表。
快照信息表

dbschema
relation
attrlist:
  attrname: 
    old: typename
    new: typename
    val: typename
ukeylist:
  [[attrname]]
  
dbschema
relation
attrlist:
  attrname:
    old: value
    new: value
    val: value

create table wallog
( keyvalue bigint
, typename bigint
, dbschema string
, relation string
, committs timestamp
// , newtuple Map<string, string>
// , oldtuple Map<string, string>
// , newukeys Array<Map<string, string>>
// , oldukeys Array<Map<string, string>>
, tupleval Map<string, string>
, pkeysval Map<string, string>
, primary key(keyval)
);

元组(tupleval)：000
关系(relation)：010(开始), 011(结束)
事务(txaction)：100(开始), 101(结束)
冲正(reversal)：110(开始), 111(结束)
