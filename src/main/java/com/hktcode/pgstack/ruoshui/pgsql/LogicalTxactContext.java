/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 复制流上下文.
 */
public class LogicalTxactContext
{
    /**
     * 构造{@link LogicalTxactContext}对象.
     *
     * @return {@link LogicalTxactContext}对象.
     */
    public static LogicalTxactContext of()
    {
        return new LogicalTxactContext();
    }
    /**
     * 当前xid信息.
     *
     * 如果是{@code 0}，表示没有当前事务xid信息.
     */
    public long xidofmsg = 0L;

    /**
     * 当前消息提交LSN.
     *
     * 如果是逻辑复制流消息，表示该事务提交消息。
     * 如果是快照信息流消息，表示该快照的一致性LSN。
     */
    public long lsnofcmt = 0L;

    /**
     * 数据库服务器地址.
     */
    public String dbserver = "";

    /**
     * 当前消息的提交时间.
     */
    public long committs = 0L;

    /**
     * 当前拥有的所有关系的结构.
     */
    public final Map<Long, PgReplRelation> relalist = new HashMap<>();

    /**
     * 当前用于的所有类型的信息.
     */
    public final Map<Long, LogicalDatatypeInfMsg> typelist;

    /**
     * 构造函数.
     */
    private LogicalTxactContext()
    {
        this.typelist = new HashMap<>();
        this.typelist.put(16L, LogicalDatatypeInfMsg.of( 16, "pg_catalog", "bool"));
        this.typelist.put(17L, LogicalDatatypeInfMsg.of( 17, "pg_catalog", "bytea"));
        this.typelist.put(18L, LogicalDatatypeInfMsg.of( 18, "pg_catalog", "char"));
        this.typelist.put(19L, LogicalDatatypeInfMsg.of( 19, "pg_catalog", "name"));
        this.typelist.put(20L, LogicalDatatypeInfMsg.of( 20, "pg_catalog", "int8"));
        this.typelist.put(21L, LogicalDatatypeInfMsg.of( 21, "pg_catalog", "int2"));
        this.typelist.put(22L, LogicalDatatypeInfMsg.of( 22, "pg_catalog", "int2vector"));
        this.typelist.put(23L, LogicalDatatypeInfMsg.of( 23, "pg_catalog", "int4"));
        this.typelist.put(24L, LogicalDatatypeInfMsg.of( 24, "pg_catalog", "regproc"));
        this.typelist.put(25L, LogicalDatatypeInfMsg.of( 25, "pg_catalog", "text"));
        this.typelist.put(26L, LogicalDatatypeInfMsg.of( 26, "pg_catalog", "oid"));
        this.typelist.put(27L, LogicalDatatypeInfMsg.of( 27, "pg_catalog", "tid"));
        this.typelist.put(28L, LogicalDatatypeInfMsg.of( 28, "pg_catalog", "xid"));
        this.typelist.put(29L, LogicalDatatypeInfMsg.of( 29, "pg_catalog", "cid"));
        this.typelist.put(30L, LogicalDatatypeInfMsg.of( 30, "pg_catalog", "oidvector"));
        this.typelist.put(71L, LogicalDatatypeInfMsg.of( 71, "pg_catalog", "pg_type"));
        this.typelist.put(75L, LogicalDatatypeInfMsg.of( 75, "pg_catalog", "pg_attribute"));
        this.typelist.put(81L, LogicalDatatypeInfMsg.of( 81, "pg_catalog", "pg_proc"));
        this.typelist.put(83L, LogicalDatatypeInfMsg.of( 83, "pg_catalog", "pg_class"));
        this.typelist.put(114L, LogicalDatatypeInfMsg.of( 114, "pg_catalog", "json"));
        this.typelist.put(142L, LogicalDatatypeInfMsg.of( 142, "pg_catalog", "xml"));
        this.typelist.put(143L, LogicalDatatypeInfMsg.of( 143, "pg_catalog", "_xml"));
        this.typelist.put(199L, LogicalDatatypeInfMsg.of( 199, "pg_catalog", "_json"));
        this.typelist.put(194L, LogicalDatatypeInfMsg.of( 194, "pg_catalog", "pg_node_tree"));
        this.typelist.put(3361L, LogicalDatatypeInfMsg.of( 3361, "pg_catalog", "pg_ndistinct"));
        this.typelist.put(3402L, LogicalDatatypeInfMsg.of( 3402, "pg_catalog", "pg_dependencies"));
        this.typelist.put(32L, LogicalDatatypeInfMsg.of( 32, "pg_catalog", "pg_ddl_command"));
        this.typelist.put(210L, LogicalDatatypeInfMsg.of( 210, "pg_catalog", "smgr"));
        this.typelist.put(600L, LogicalDatatypeInfMsg.of( 600, "pg_catalog", "point"));
        this.typelist.put(601L, LogicalDatatypeInfMsg.of( 601, "pg_catalog", "lseg"));
        this.typelist.put(602L, LogicalDatatypeInfMsg.of( 602, "pg_catalog", "path"));
        this.typelist.put(603L, LogicalDatatypeInfMsg.of( 603, "pg_catalog", "box"));
        this.typelist.put(604L, LogicalDatatypeInfMsg.of( 604, "pg_catalog", "polygon"));
        this.typelist.put(628L, LogicalDatatypeInfMsg.of( 628, "pg_catalog", "line"));
        this.typelist.put(629L, LogicalDatatypeInfMsg.of( 629, "pg_catalog", "_line"));
        this.typelist.put(700L, LogicalDatatypeInfMsg.of( 700, "pg_catalog", "float4"));
        this.typelist.put(701L, LogicalDatatypeInfMsg.of( 701, "pg_catalog", "float8"));
        this.typelist.put(702L, LogicalDatatypeInfMsg.of( 702, "pg_catalog", "abstime"));
        this.typelist.put(703L, LogicalDatatypeInfMsg.of( 703, "pg_catalog", "reltime"));
        this.typelist.put(704L, LogicalDatatypeInfMsg.of( 704, "pg_catalog", "tinterval"));
        this.typelist.put(705L, LogicalDatatypeInfMsg.of( 705, "pg_catalog", "unknown"));
        this.typelist.put(718L, LogicalDatatypeInfMsg.of( 718, "pg_catalog", "circle"));
        this.typelist.put(719L, LogicalDatatypeInfMsg.of( 719, "pg_catalog", "_circle"));
        this.typelist.put(790L, LogicalDatatypeInfMsg.of( 790, "pg_catalog", "money"));
        this.typelist.put(791L, LogicalDatatypeInfMsg.of( 791, "pg_catalog", "_money"));
        this.typelist.put(829L, LogicalDatatypeInfMsg.of( 829, "pg_catalog", "macaddr"));
        this.typelist.put(869L, LogicalDatatypeInfMsg.of( 869, "pg_catalog", "inet"));
        this.typelist.put(650L, LogicalDatatypeInfMsg.of( 650, "pg_catalog", "cidr"));
        this.typelist.put(774L, LogicalDatatypeInfMsg.of( 774, "pg_catalog", "macaddr8"));
        this.typelist.put(1000L, LogicalDatatypeInfMsg.of( 1000, "pg_catalog", "_bool"));
        this.typelist.put(1001L, LogicalDatatypeInfMsg.of( 1001, "pg_catalog", "_bytea"));
        this.typelist.put(1002L, LogicalDatatypeInfMsg.of( 1002, "pg_catalog", "_char"));
        this.typelist.put(1003L, LogicalDatatypeInfMsg.of( 1003, "pg_catalog", "_name"));
        this.typelist.put(1005L, LogicalDatatypeInfMsg.of( 1005, "pg_catalog", "_int2"));
        this.typelist.put(1006L, LogicalDatatypeInfMsg.of( 1006, "pg_catalog", "_int2vector"));
        this.typelist.put(1007L, LogicalDatatypeInfMsg.of( 1007, "pg_catalog", "_int4"));
        this.typelist.put(1008L, LogicalDatatypeInfMsg.of( 1008, "pg_catalog", "_regproc"));
        this.typelist.put(1009L, LogicalDatatypeInfMsg.of( 1009, "pg_catalog", "_text"));
        this.typelist.put(1028L, LogicalDatatypeInfMsg.of( 1028, "pg_catalog", "_oid"));
        this.typelist.put(1010L, LogicalDatatypeInfMsg.of( 1010, "pg_catalog", "_tid"));
        this.typelist.put(1011L, LogicalDatatypeInfMsg.of( 1011, "pg_catalog", "_xid"));
        this.typelist.put(1012L, LogicalDatatypeInfMsg.of( 1012, "pg_catalog", "_cid"));
        this.typelist.put(1013L, LogicalDatatypeInfMsg.of( 1013, "pg_catalog", "_oidvector"));
        this.typelist.put(1014L, LogicalDatatypeInfMsg.of( 1014, "pg_catalog", "_bpchar"));
        this.typelist.put(1015L, LogicalDatatypeInfMsg.of( 1015, "pg_catalog", "_varchar"));
        this.typelist.put(1016L, LogicalDatatypeInfMsg.of( 1016, "pg_catalog", "_int8"));
        this.typelist.put(1017L, LogicalDatatypeInfMsg.of( 1017, "pg_catalog", "_point"));
        this.typelist.put(1018L, LogicalDatatypeInfMsg.of( 1018, "pg_catalog", "_lseg"));
        this.typelist.put(1019L, LogicalDatatypeInfMsg.of( 1019, "pg_catalog", "_path"));
        this.typelist.put(1020L, LogicalDatatypeInfMsg.of( 1020, "pg_catalog", "_box"));
        this.typelist.put(1021L, LogicalDatatypeInfMsg.of( 1021, "pg_catalog", "_float4"));
        this.typelist.put(1022L, LogicalDatatypeInfMsg.of( 1022, "pg_catalog", "_float8"));
        this.typelist.put(1023L, LogicalDatatypeInfMsg.of( 1023, "pg_catalog", "_abstime"));
        this.typelist.put(1024L, LogicalDatatypeInfMsg.of( 1024, "pg_catalog", "_reltime"));
        this.typelist.put(1025L, LogicalDatatypeInfMsg.of( 1025, "pg_catalog", "_tinterval"));
        this.typelist.put(1027L, LogicalDatatypeInfMsg.of( 1027, "pg_catalog", "_polygon"));
        this.typelist.put(1033L, LogicalDatatypeInfMsg.of( 1033, "pg_catalog", "aclitem"));
        this.typelist.put(1034L, LogicalDatatypeInfMsg.of( 1034, "pg_catalog", "_aclitem"));
        this.typelist.put(1040L, LogicalDatatypeInfMsg.of( 1040, "pg_catalog", "_macaddr"));
        this.typelist.put(775L, LogicalDatatypeInfMsg.of( 775, "pg_catalog", "_macaddr8"));
        this.typelist.put(1041L, LogicalDatatypeInfMsg.of( 1041, "pg_catalog", "_inet"));
        this.typelist.put(651L, LogicalDatatypeInfMsg.of( 651, "pg_catalog", "_cidr"));
        this.typelist.put(1263L, LogicalDatatypeInfMsg.of( 1263, "pg_catalog", "_cstring"));
        this.typelist.put(1042L, LogicalDatatypeInfMsg.of( 1042, "pg_catalog", "bpchar"));
        this.typelist.put(1043L, LogicalDatatypeInfMsg.of( 1043, "pg_catalog", "varchar"));
        this.typelist.put(1082L, LogicalDatatypeInfMsg.of( 1082, "pg_catalog", "date"));
        this.typelist.put(1083L, LogicalDatatypeInfMsg.of( 1083, "pg_catalog", "time"));
        this.typelist.put(1114L, LogicalDatatypeInfMsg.of( 1114, "pg_catalog", "timestamp"));
        this.typelist.put(1115L, LogicalDatatypeInfMsg.of( 1115, "pg_catalog", "_timestamp"));
        this.typelist.put(1182L, LogicalDatatypeInfMsg.of( 1182, "pg_catalog", "_date"));
        this.typelist.put(1183L, LogicalDatatypeInfMsg.of( 1183, "pg_catalog", "_time"));
        this.typelist.put(1184L, LogicalDatatypeInfMsg.of( 1184, "pg_catalog", "timestamptz"));
        this.typelist.put(1185L, LogicalDatatypeInfMsg.of( 1185, "pg_catalog", "_timestamptz"));
        this.typelist.put(1186L, LogicalDatatypeInfMsg.of( 1186, "pg_catalog", "interval"));
        this.typelist.put(1187L, LogicalDatatypeInfMsg.of( 1187, "pg_catalog", "_interval"));
        this.typelist.put(1231L, LogicalDatatypeInfMsg.of( 1231, "pg_catalog", "_numeric"));
        this.typelist.put(1266L, LogicalDatatypeInfMsg.of( 1266, "pg_catalog", "timetz"));
        this.typelist.put(1270L, LogicalDatatypeInfMsg.of( 1270, "pg_catalog", "_timetz"));
        this.typelist.put(1560L, LogicalDatatypeInfMsg.of( 1560, "pg_catalog", "bit"));
        this.typelist.put(1561L, LogicalDatatypeInfMsg.of( 1561, "pg_catalog", "_bit"));
        this.typelist.put(1562L, LogicalDatatypeInfMsg.of( 1562, "pg_catalog", "varbit"));
        this.typelist.put(1563L, LogicalDatatypeInfMsg.of( 1563, "pg_catalog", "_varbit"));
        this.typelist.put(1700L, LogicalDatatypeInfMsg.of( 1700, "pg_catalog", "numeric"));
        this.typelist.put(1790L, LogicalDatatypeInfMsg.of( 1790, "pg_catalog", "refcursor"));
        this.typelist.put(2201L, LogicalDatatypeInfMsg.of( 2201, "pg_catalog", "_refcursor"));
        this.typelist.put(2202L, LogicalDatatypeInfMsg.of( 2202, "pg_catalog", "regprocedure"));
        this.typelist.put(2203L, LogicalDatatypeInfMsg.of( 2203, "pg_catalog", "regoper"));
        this.typelist.put(2204L, LogicalDatatypeInfMsg.of( 2204, "pg_catalog", "regoperator"));
        this.typelist.put(2205L, LogicalDatatypeInfMsg.of( 2205, "pg_catalog", "regclass"));
        this.typelist.put(2206L, LogicalDatatypeInfMsg.of( 2206, "pg_catalog", "regtype"));
        this.typelist.put(4096L, LogicalDatatypeInfMsg.of( 4096, "pg_catalog", "regrole"));
        this.typelist.put(4089L, LogicalDatatypeInfMsg.of( 4089, "pg_catalog", "regnamespace"));
        this.typelist.put(2207L, LogicalDatatypeInfMsg.of( 2207, "pg_catalog", "_regprocedure"));
        this.typelist.put(2208L, LogicalDatatypeInfMsg.of( 2208, "pg_catalog", "_regoper"));
        this.typelist.put(2209L, LogicalDatatypeInfMsg.of( 2209, "pg_catalog", "_regoperator"));
        this.typelist.put(2210L, LogicalDatatypeInfMsg.of( 2210, "pg_catalog", "_regclass"));
        this.typelist.put(2211L, LogicalDatatypeInfMsg.of( 2211, "pg_catalog", "_regtype"));
        this.typelist.put(4097L, LogicalDatatypeInfMsg.of( 4097, "pg_catalog", "_regrole"));
        this.typelist.put(4090L, LogicalDatatypeInfMsg.of( 4090, "pg_catalog", "_regnamespace"));
        this.typelist.put(2950L, LogicalDatatypeInfMsg.of( 2950, "pg_catalog", "uuid"));
        this.typelist.put(2951L, LogicalDatatypeInfMsg.of( 2951, "pg_catalog", "_uuid"));
        this.typelist.put(3220L, LogicalDatatypeInfMsg.of( 3220, "pg_catalog", "pg_lsn"));
        this.typelist.put(3221L, LogicalDatatypeInfMsg.of( 3221, "pg_catalog", "_pg_lsn"));
        this.typelist.put(3614L, LogicalDatatypeInfMsg.of( 3614, "pg_catalog", "tsvector"));
        this.typelist.put(3642L, LogicalDatatypeInfMsg.of( 3642, "pg_catalog", "gtsvector"));
        this.typelist.put(3615L, LogicalDatatypeInfMsg.of( 3615, "pg_catalog", "tsquery"));
        this.typelist.put(3734L, LogicalDatatypeInfMsg.of( 3734, "pg_catalog", "regconfig"));
        this.typelist.put(3769L, LogicalDatatypeInfMsg.of( 3769, "pg_catalog", "regdictionary"));
        this.typelist.put(3643L, LogicalDatatypeInfMsg.of( 3643, "pg_catalog", "_tsvector"));
        this.typelist.put(3644L, LogicalDatatypeInfMsg.of( 3644, "pg_catalog", "_gtsvector"));
        this.typelist.put(3645L, LogicalDatatypeInfMsg.of( 3645, "pg_catalog", "_tsquery"));
        this.typelist.put(3735L, LogicalDatatypeInfMsg.of( 3735, "pg_catalog", "_regconfig"));
        this.typelist.put(3770L, LogicalDatatypeInfMsg.of( 3770, "pg_catalog", "_regdictionary"));
        this.typelist.put(3802L, LogicalDatatypeInfMsg.of( 3802, "pg_catalog", "jsonb"));
        this.typelist.put(3807L, LogicalDatatypeInfMsg.of( 3807, "pg_catalog", "_jsonb"));
        this.typelist.put(2970L, LogicalDatatypeInfMsg.of( 2970, "pg_catalog", "txid_snapshot"));
        this.typelist.put(2949L, LogicalDatatypeInfMsg.of( 2949, "pg_catalog", "_txid_snapshot"));
        this.typelist.put(3904L, LogicalDatatypeInfMsg.of( 3904, "pg_catalog", "int4range"));
        this.typelist.put(3905L, LogicalDatatypeInfMsg.of( 3905, "pg_catalog", "_int4range"));
        this.typelist.put(3906L, LogicalDatatypeInfMsg.of( 3906, "pg_catalog", "numrange"));
        this.typelist.put(3907L, LogicalDatatypeInfMsg.of( 3907, "pg_catalog", "_numrange"));
        this.typelist.put(3908L, LogicalDatatypeInfMsg.of( 3908, "pg_catalog", "tsrange"));
        this.typelist.put(3909L, LogicalDatatypeInfMsg.of( 3909, "pg_catalog", "_tsrange"));
        this.typelist.put(3910L, LogicalDatatypeInfMsg.of( 3910, "pg_catalog", "tstzrange"));
        this.typelist.put(3911L, LogicalDatatypeInfMsg.of( 3911, "pg_catalog", "_tstzrange"));
        this.typelist.put(3912L, LogicalDatatypeInfMsg.of( 3912, "pg_catalog", "daterange"));
        this.typelist.put(3913L, LogicalDatatypeInfMsg.of( 3913, "pg_catalog", "_daterange"));
        this.typelist.put(3926L, LogicalDatatypeInfMsg.of( 3926, "pg_catalog", "int8range"));
        this.typelist.put(3927L, LogicalDatatypeInfMsg.of( 3927, "pg_catalog", "_int8range"));
        this.typelist.put(2249L, LogicalDatatypeInfMsg.of( 2249, "pg_catalog", "record"));
        this.typelist.put(2287L, LogicalDatatypeInfMsg.of( 2287, "pg_catalog", "_record"));
        this.typelist.put(2275L, LogicalDatatypeInfMsg.of( 2275, "pg_catalog", "cstring"));
        this.typelist.put(2276L, LogicalDatatypeInfMsg.of( 2276, "pg_catalog", "any"));
        this.typelist.put(2277L, LogicalDatatypeInfMsg.of( 2277, "pg_catalog", "anyarray"));
        this.typelist.put(2278L, LogicalDatatypeInfMsg.of( 2278, "pg_catalog", "void"));
        this.typelist.put(2279L, LogicalDatatypeInfMsg.of( 2279, "pg_catalog", "trigger"));
        this.typelist.put(3838L, LogicalDatatypeInfMsg.of( 3838, "pg_catalog", "event_trigger"));
        this.typelist.put(2280L, LogicalDatatypeInfMsg.of( 2280, "pg_catalog", "language_handler"));
        this.typelist.put(2281L, LogicalDatatypeInfMsg.of( 2281, "pg_catalog", "internal"));
        this.typelist.put(2282L, LogicalDatatypeInfMsg.of( 2282, "pg_catalog", "opaque"));
        this.typelist.put(2283L, LogicalDatatypeInfMsg.of( 2283, "pg_catalog", "anyelement"));
        this.typelist.put(2776L, LogicalDatatypeInfMsg.of( 2776, "pg_catalog", "anynonarray"));
        this.typelist.put(3500L, LogicalDatatypeInfMsg.of( 3500, "pg_catalog", "anyenum"));
        this.typelist.put(3115L, LogicalDatatypeInfMsg.of( 3115, "pg_catalog", "fdw_handler"));
        this.typelist.put(325L, LogicalDatatypeInfMsg.of( 325, "pg_catalog", "index_am_handler"));
        this.typelist.put(3310L, LogicalDatatypeInfMsg.of( 3310, "pg_catalog", "tsm_handler"));
        this.typelist.put(3831L, LogicalDatatypeInfMsg.of( 3831, "pg_catalog", "anyrange"));
        this.typelist.put(10000L, LogicalDatatypeInfMsg.of( 10000, "pg_catalog", "pg_attrdef"));
        this.typelist.put(10001L, LogicalDatatypeInfMsg.of( 10001, "pg_catalog", "pg_constraint"));
        this.typelist.put(10002L, LogicalDatatypeInfMsg.of( 10002, "pg_catalog", "pg_inherits"));
        this.typelist.put(10003L, LogicalDatatypeInfMsg.of( 10003, "pg_catalog", "pg_index"));
        this.typelist.put(10004L, LogicalDatatypeInfMsg.of( 10004, "pg_catalog", "pg_operator"));
        this.typelist.put(10005L, LogicalDatatypeInfMsg.of( 10005, "pg_catalog", "pg_opfamily"));
        this.typelist.put(10006L, LogicalDatatypeInfMsg.of( 10006, "pg_catalog", "pg_opclass"));
        this.typelist.put(10130L, LogicalDatatypeInfMsg.of( 10130, "pg_catalog", "pg_am"));
        this.typelist.put(10131L, LogicalDatatypeInfMsg.of( 10131, "pg_catalog", "pg_amop"));
        this.typelist.put(10841L, LogicalDatatypeInfMsg.of( 10841, "pg_catalog", "pg_amproc"));
        this.typelist.put(11253L, LogicalDatatypeInfMsg.of( 11253, "pg_catalog", "pg_language"));
        this.typelist.put(11254L, LogicalDatatypeInfMsg.of( 11254, "pg_catalog", "pg_largeobject_metadata"));
        this.typelist.put(11255L, LogicalDatatypeInfMsg.of( 11255, "pg_catalog", "pg_largeobject"));
        this.typelist.put(11256L, LogicalDatatypeInfMsg.of( 11256, "pg_catalog", "pg_aggregate"));
        this.typelist.put(11257L, LogicalDatatypeInfMsg.of( 11257, "pg_catalog", "pg_statistic_ext"));
        this.typelist.put(11258L, LogicalDatatypeInfMsg.of( 11258, "pg_catalog", "pg_statistic"));
        this.typelist.put(11259L, LogicalDatatypeInfMsg.of( 11259, "pg_catalog", "pg_rewrite"));
        this.typelist.put(11260L, LogicalDatatypeInfMsg.of( 11260, "pg_catalog", "pg_trigger"));
        this.typelist.put(11261L, LogicalDatatypeInfMsg.of( 11261, "pg_catalog", "pg_event_trigger"));
        this.typelist.put(11262L, LogicalDatatypeInfMsg.of( 11262, "pg_catalog", "pg_description"));
        this.typelist.put(11263L, LogicalDatatypeInfMsg.of( 11263, "pg_catalog", "pg_cast"));
        this.typelist.put(11483L, LogicalDatatypeInfMsg.of( 11483, "pg_catalog", "pg_enum"));
        this.typelist.put(11484L, LogicalDatatypeInfMsg.of( 11484, "pg_catalog", "pg_namespace"));
        this.typelist.put(11485L, LogicalDatatypeInfMsg.of( 11485, "pg_catalog", "pg_conversion"));
        this.typelist.put(11486L, LogicalDatatypeInfMsg.of( 11486, "pg_catalog", "pg_depend"));
        this.typelist.put(1248L, LogicalDatatypeInfMsg.of( 1248, "pg_catalog", "pg_database"));
        this.typelist.put(11487L, LogicalDatatypeInfMsg.of( 11487, "pg_catalog", "pg_db_role_setting"));
        this.typelist.put(11488L, LogicalDatatypeInfMsg.of( 11488, "pg_catalog", "pg_tablespace"));
        this.typelist.put(11489L, LogicalDatatypeInfMsg.of( 11489, "pg_catalog", "pg_pltemplate"));
        this.typelist.put(2842L, LogicalDatatypeInfMsg.of( 2842, "pg_catalog", "pg_authid"));
        this.typelist.put(2843L, LogicalDatatypeInfMsg.of( 2843, "pg_catalog", "pg_auth_members"));
        this.typelist.put(11490L, LogicalDatatypeInfMsg.of( 11490, "pg_catalog", "pg_shdepend"));
        this.typelist.put(11491L, LogicalDatatypeInfMsg.of( 11491, "pg_catalog", "pg_shdescription"));
        this.typelist.put(11492L, LogicalDatatypeInfMsg.of( 11492, "pg_catalog", "pg_ts_config"));
        this.typelist.put(11493L, LogicalDatatypeInfMsg.of( 11493, "pg_catalog", "pg_ts_config_map"));
        this.typelist.put(11494L, LogicalDatatypeInfMsg.of( 11494, "pg_catalog", "pg_ts_dict"));
        this.typelist.put(11495L, LogicalDatatypeInfMsg.of( 11495, "pg_catalog", "pg_ts_parser"));
        this.typelist.put(11496L, LogicalDatatypeInfMsg.of( 11496, "pg_catalog", "pg_ts_template"));
        this.typelist.put(11497L, LogicalDatatypeInfMsg.of( 11497, "pg_catalog", "pg_extension"));
        this.typelist.put(11498L, LogicalDatatypeInfMsg.of( 11498, "pg_catalog", "pg_foreign_data_wrapper"));
        this.typelist.put(11499L, LogicalDatatypeInfMsg.of( 11499, "pg_catalog", "pg_foreign_server"));
        this.typelist.put(11500L, LogicalDatatypeInfMsg.of( 11500, "pg_catalog", "pg_user_mapping"));
        this.typelist.put(11501L, LogicalDatatypeInfMsg.of( 11501, "pg_catalog", "pg_foreign_table"));
        this.typelist.put(11502L, LogicalDatatypeInfMsg.of( 11502, "pg_catalog", "pg_policy"));
        this.typelist.put(11503L, LogicalDatatypeInfMsg.of( 11503, "pg_catalog", "pg_replication_origin"));
        this.typelist.put(11504L, LogicalDatatypeInfMsg.of( 11504, "pg_catalog", "pg_default_acl"));
        this.typelist.put(11505L, LogicalDatatypeInfMsg.of( 11505, "pg_catalog", "pg_init_privs"));
        this.typelist.put(11506L, LogicalDatatypeInfMsg.of( 11506, "pg_catalog", "pg_seclabel"));
        this.typelist.put(4066L, LogicalDatatypeInfMsg.of( 4066, "pg_catalog", "pg_shseclabel"));
        this.typelist.put(11507L, LogicalDatatypeInfMsg.of( 11507, "pg_catalog", "pg_collation"));
        this.typelist.put(11508L, LogicalDatatypeInfMsg.of( 11508, "pg_catalog", "pg_partitioned_table"));
        this.typelist.put(11509L, LogicalDatatypeInfMsg.of( 11509, "pg_catalog", "pg_range"));
        this.typelist.put(11510L, LogicalDatatypeInfMsg.of( 11510, "pg_catalog", "pg_transform"));
        this.typelist.put(11511L, LogicalDatatypeInfMsg.of( 11511, "pg_catalog", "pg_sequence"));
        this.typelist.put(11512L, LogicalDatatypeInfMsg.of( 11512, "pg_catalog", "pg_publication"));
        this.typelist.put(11513L, LogicalDatatypeInfMsg.of( 11513, "pg_catalog", "pg_publication_rel"));
        this.typelist.put(6101L, LogicalDatatypeInfMsg.of( 6101, "pg_catalog", "pg_subscription"));
        this.typelist.put(11514L, LogicalDatatypeInfMsg.of( 11514, "pg_catalog", "pg_subscription_rel"));
        this.typelist.put(11528L, LogicalDatatypeInfMsg.of( 11528, "pg_catalog", "pg_roles"));
        this.typelist.put(11532L, LogicalDatatypeInfMsg.of( 11532, "pg_catalog", "pg_shadow"));
        this.typelist.put(11536L, LogicalDatatypeInfMsg.of( 11536, "pg_catalog", "pg_group"));
        this.typelist.put(11539L, LogicalDatatypeInfMsg.of( 11539, "pg_catalog", "pg_user"));
        this.typelist.put(11542L, LogicalDatatypeInfMsg.of( 11542, "pg_catalog", "pg_policies"));
        this.typelist.put(11546L, LogicalDatatypeInfMsg.of( 11546, "pg_catalog", "pg_rules"));
        this.typelist.put(11550L, LogicalDatatypeInfMsg.of( 11550, "pg_catalog", "pg_views"));
        this.typelist.put(11554L, LogicalDatatypeInfMsg.of( 11554, "pg_catalog", "pg_tables"));
        this.typelist.put(11558L, LogicalDatatypeInfMsg.of( 11558, "pg_catalog", "pg_matviews"));
        this.typelist.put(11562L, LogicalDatatypeInfMsg.of( 11562, "pg_catalog", "pg_indexes"));
        this.typelist.put(11566L, LogicalDatatypeInfMsg.of( 11566, "pg_catalog", "pg_sequences"));
        this.typelist.put(11570L, LogicalDatatypeInfMsg.of( 11570, "pg_catalog", "pg_stats"));
        this.typelist.put(11574L, LogicalDatatypeInfMsg.of( 11574, "pg_catalog", "pg_publication_tables"));
        this.typelist.put(11578L, LogicalDatatypeInfMsg.of( 11578, "pg_catalog", "pg_locks"));
        this.typelist.put(11581L, LogicalDatatypeInfMsg.of( 11581, "pg_catalog", "pg_cursors"));
        this.typelist.put(11584L, LogicalDatatypeInfMsg.of( 11584, "pg_catalog", "pg_available_extensions"));
        this.typelist.put(11587L, LogicalDatatypeInfMsg.of( 11587, "pg_catalog", "pg_available_extension_versions"));
        this.typelist.put(11590L, LogicalDatatypeInfMsg.of( 11590, "pg_catalog", "pg_prepared_xacts"));
        this.typelist.put(11594L, LogicalDatatypeInfMsg.of( 11594, "pg_catalog", "pg_prepared_statements"));
        this.typelist.put(11597L, LogicalDatatypeInfMsg.of( 11597, "pg_catalog", "pg_seclabels"));
        this.typelist.put(11601L, LogicalDatatypeInfMsg.of( 11601, "pg_catalog", "pg_settings"));
        this.typelist.put(11606L, LogicalDatatypeInfMsg.of( 11606, "pg_catalog", "pg_file_settings"));
        this.typelist.put(11609L, LogicalDatatypeInfMsg.of( 11609, "pg_catalog", "pg_hba_file_rules"));
        this.typelist.put(11612L, LogicalDatatypeInfMsg.of( 11612, "pg_catalog", "pg_timezone_abbrevs"));
        this.typelist.put(11615L, LogicalDatatypeInfMsg.of( 11615, "pg_catalog", "pg_timezone_names"));
        this.typelist.put(11618L, LogicalDatatypeInfMsg.of( 11618, "pg_catalog", "pg_config"));
        this.typelist.put(11621L, LogicalDatatypeInfMsg.of( 11621, "pg_catalog", "pg_stat_all_tables"));
        this.typelist.put(11625L, LogicalDatatypeInfMsg.of( 11625, "pg_catalog", "pg_stat_xact_all_tables"));
        this.typelist.put(11629L, LogicalDatatypeInfMsg.of( 11629, "pg_catalog", "pg_stat_sys_tables"));
        this.typelist.put(11633L, LogicalDatatypeInfMsg.of( 11633, "pg_catalog", "pg_stat_xact_sys_tables"));
        this.typelist.put(11636L, LogicalDatatypeInfMsg.of( 11636, "pg_catalog", "pg_stat_user_tables"));
        this.typelist.put(11640L, LogicalDatatypeInfMsg.of( 11640, "pg_catalog", "pg_stat_xact_user_tables"));
        this.typelist.put(11643L, LogicalDatatypeInfMsg.of( 11643, "pg_catalog", "pg_statio_all_tables"));
        this.typelist.put(11647L, LogicalDatatypeInfMsg.of( 11647, "pg_catalog", "pg_statio_sys_tables"));
        this.typelist.put(11650L, LogicalDatatypeInfMsg.of( 11650, "pg_catalog", "pg_statio_user_tables"));
        this.typelist.put(11653L, LogicalDatatypeInfMsg.of( 11653, "pg_catalog", "pg_stat_all_indexes"));
        this.typelist.put(11657L, LogicalDatatypeInfMsg.of( 11657, "pg_catalog", "pg_stat_sys_indexes"));
        this.typelist.put(11660L, LogicalDatatypeInfMsg.of( 11660, "pg_catalog", "pg_stat_user_indexes"));
        this.typelist.put(11663L, LogicalDatatypeInfMsg.of( 11663, "pg_catalog", "pg_statio_all_indexes"));
        this.typelist.put(11667L, LogicalDatatypeInfMsg.of( 11667, "pg_catalog", "pg_statio_sys_indexes"));
        this.typelist.put(11670L, LogicalDatatypeInfMsg.of( 11670, "pg_catalog", "pg_statio_user_indexes"));
        this.typelist.put(11673L, LogicalDatatypeInfMsg.of( 11673, "pg_catalog", "pg_statio_all_sequences"));
        this.typelist.put(11677L, LogicalDatatypeInfMsg.of( 11677, "pg_catalog", "pg_statio_sys_sequences"));
        this.typelist.put(11680L, LogicalDatatypeInfMsg.of( 11680, "pg_catalog", "pg_statio_user_sequences"));
        this.typelist.put(11683L, LogicalDatatypeInfMsg.of( 11683, "pg_catalog", "pg_stat_activity"));
        this.typelist.put(11687L, LogicalDatatypeInfMsg.of( 11687, "pg_catalog", "pg_stat_replication"));
        this.typelist.put(11691L, LogicalDatatypeInfMsg.of( 11691, "pg_catalog", "pg_stat_wal_receiver"));
        this.typelist.put(11694L, LogicalDatatypeInfMsg.of( 11694, "pg_catalog", "pg_stat_subscription"));
        this.typelist.put(11697L, LogicalDatatypeInfMsg.of( 11697, "pg_catalog", "pg_stat_ssl"));
        this.typelist.put(11700L, LogicalDatatypeInfMsg.of( 11700, "pg_catalog", "pg_replication_slots"));
        this.typelist.put(11704L, LogicalDatatypeInfMsg.of( 11704, "pg_catalog", "pg_stat_database"));
        this.typelist.put(11707L, LogicalDatatypeInfMsg.of( 11707, "pg_catalog", "pg_stat_database_conflicts"));
        this.typelist.put(11710L, LogicalDatatypeInfMsg.of( 11710, "pg_catalog", "pg_stat_user_functions"));
        this.typelist.put(11714L, LogicalDatatypeInfMsg.of( 11714, "pg_catalog", "pg_stat_xact_user_functions"));
        this.typelist.put(11718L, LogicalDatatypeInfMsg.of( 11718, "pg_catalog", "pg_stat_archiver"));
        this.typelist.put(11721L, LogicalDatatypeInfMsg.of( 11721, "pg_catalog", "pg_stat_bgwriter"));
        this.typelist.put(11724L, LogicalDatatypeInfMsg.of( 11724, "pg_catalog", "pg_stat_progress_vacuum"));
        this.typelist.put(11728L, LogicalDatatypeInfMsg.of( 11728, "pg_catalog", "pg_user_mappings"));
        this.typelist.put(11732L, LogicalDatatypeInfMsg.of( 11732, "pg_catalog", "pg_replication_origin_status"));
    }

    /**
     * 将接收到的{@code LogicalRelationInfMsg}消息存入{@link #relalist}.
     *
     * @param msg 接收到的{@link LogicalRelationInfMsg}消息.
     * @throws ArgumentNullException if {@code msg} is {@code null}.
     */
    public void putRelation(LogicalRelationInfMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        PgReplRelationMetadata metadata = PgReplRelationMetadata.of //
            /* */( msg.relident //
            /* */, msg.dbschema //
            /* */, msg.relation //
            /* */, msg.replchar //
            /* */);
        List<PgReplAttribute> attrlist = new ArrayList<>();
        for (LogicalAttribute attr : msg.attrlist) {
            LogicalDatatypeInfMsg type = this.typelist.get(attr.datatype);
            if (type == null) {
                throw new RuntimeException(); // TODO:
            }
            PgReplAttribute attrinfo = PgReplAttribute.of //
                /* */( attr.attrname //
                /* */, type.tpschema //
                /* */, type.typename //
                /* */, -1 //
                /* */, attr.attflags //
                /* */, attr.datatype //
                /* */, attr.attypmod //
                /* */);
            attrlist.add(attrinfo);
        }
        PgReplRelation relation //
            = PgReplRelation.of(metadata, ImmutableList.copyOf(attrlist));
        this.relalist.put(msg.relident, relation);
    }
}
