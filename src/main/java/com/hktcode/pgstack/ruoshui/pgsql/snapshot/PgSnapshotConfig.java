/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgtriple.naive.NaiveConfig;
import com.hktcode.pgjdbc.*;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;

import javax.script.ScriptException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询快照时的配置.
 *
 * TODO: 需要重构.
 */
public class PgSnapshotConfig
{
    /**
     * 默认查询关系metadata方法.
     */
    public static final String DEFAULT_RELATION_SQL = "" //
        + "\n SELECT                     \"t\".\"oid\"::int8 as \"relident\"" //
        + "\n      ,                       \"n\".\"nspname\" as \"dbschema\"" //
        + "\n      ,                       \"t\".\"relname\" as \"relation\"" //
        + "\n      , \"ascii\"(\"t\".\"relreplident\")::int8 as \"replchar\"" //
        + "\n FROM            \"pg_catalog\".\"pg_class\"     \"t\"" //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_namespace\" \"n\"" //
        + "\n              ON \"t\".\"relnamespace\" = \"n\".\"oid\"" //
        + "\n WHERE     \"t\".\"relpersistence\" = 'p' " //
        + "\n       and \"t\".\"relkind\" in ('r', 'p') " //
        + "\n       and \"n\".\"nspname\" not in ('information_schema', 'pg_catalog') " //
        + "\n       and \"n\".\"nspname\" not like 'pg_temp%' " //
        + "\n       and \"n\".\"nspname\" not like 'pg_toast%' " //
        + "\n";

    /**
     * 默认查询关系属性方法.
     */
    public static final String DEFAULT_ATTRINFO_SQL = "" //
        +"\n SELECT (case when \"k\".\"conrelid\" is null then 0 else 1 end)::int8 as \"attflags\" " //
        +"\n      ,                                              \"a\".\"attname\" as \"attrname\" " //
        +"\n      ,                                       \"a\".\"atttypid\"::int8 as \"datatype\" " //
        +"\n      ,                                      \"a\".\"atttypmod\"::int8 as \"attypmod\" " //
        +"\n      ,                                              \"n\".\"nspname\" as \"tpschema\" " //
        +"\n      ,                                              \"t\".\"typname\" as \"typename\" " //
        +"\n FROM            \"pg_catalog\".\"pg_attribute\" a " //
        +"\n      INNER JOIN \"pg_catalog\".\"pg_type\" t " //
        +"\n              ON \"a\".\"atttypid\" = \"t\".\"oid\" " //
        +"\n      INNER JOIN \"pg_catalog\".\"pg_namespace\" n " //
        +"\n              ON \"t\".\"typnamespace\" = n.\"oid\" " //
        +"\n       LEFT JOIN ( select     \"c\".\"conrelid\" as \"conrelid\" " //
        +"\n                        , \"unnest\"(\"conkey\") as \"conkey\" " //
        +"\n                   from            \"pg_catalog\".\"pg_constraint\" c " //
        +"\n                        inner join ( select \"c\".\"conrelid\" as \"conrelid\" " //
        +"\n                                          , \"min\"(\"c\".\"oid\") as \"oid\" " //
        +"\n                                     from            \"pg_catalog\".\"pg_constraint\" \"c\" " //
        +"\n                                          inner join ( select \"conrelid\" as \"conrelid\" " //
        +"\n                                                            , max(\"contype\") as \"contype\" " //
        +"\n                                                       from \"pg_catalog\".\"pg_constraint\" " //
        +"\n                                                       where     \"contype\" in ('p', 'u') " //
        +"\n                                                             and \"conrelid\" <> 0 " //
        +"\n                                                       group by \"conrelid\" " //
        +"\n                                                     ) m " //
        +"\n                                                  on     m.\"conrelid\" = c.\"conrelid\" " //
        +"\n                                                     and m.\"contype\" = c.\"contype\" " //
        +"\n                                     group by c.\"conrelid\" " //
        +"\n                                   ) m " //
        +"\n                                on m.\"oid\" = c.\"oid\" " //
        +"\n                 ) k " //
        +"\n              on     a.\"attrelid\" = k.\"conrelid\" " //
        +"\n                 and a.\"attnum\" = k.\"conkey\" " //
        +"\n WHERE \"a\".\"attrelid\" = ? and not \"a\".\"attisdropped\" and \"a\".\"attnum\" > 0 " //
        +"\n ORDER BY \"a\".\"attnum\" " //
        +"\n";

    /**
     * 默认的{@link ResultSet#setFetchSize(int)}值.
     */
    public static final int DEFAULT_RS_FETCHISIZE = 128;

    public static PgSnapshotConfig of //
        /* */(PgConnectionProperty srcProperty //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */, PgSnapshotFilter whereScript //
        /* */, String metadataSql //
        /* */, String attrinfoSql //
        /* */, boolean isTemporary //
        /* */, String slotnameInf //
        /* */) //
    {
        return new PgSnapshotConfig //
            /* */( srcProperty //
            /* */, tupleSelect //
            /* */, whereScript //
            /* */, metadataSql //
            /* */, attrinfoSql //
            /* */, isTemporary //
            /* */, slotnameInf //
            /* */);
    }

    /**
     * 连接属性.
     */
    public final PgConnectionProperty srcProperty;

    /**
     * 查询关系metatdata的SQL语句.
     */
    public final String metadataSql;

    /**
     * 查询关系属性的SQL语句.
     */
    public final String attrinfoSql;

    /**
     * 针对特定关系的查询语句.
     *
     * 如果没有指定，则采用默认的查询语句.
     */
    public final ImmutableMap<PgReplRelationName, String> tupleSelect;

    /**
     * 查询出关系的结构后的过滤条件.
     */
    public final PgSnapshotFilter whereScript;

    /**
     * 是不是临时复制槽.
     */
    public final boolean isTemporary;

    /**
     * 复制槽名称信息.
     */
    public final String slotnameInf;

    /**
     * 查询关系时设置ResultSet的FetchSize.
     */
    public int rsFetchsize = DEFAULT_RS_FETCHISIZE;

    /**
     * 等待时间.
     */
    public long waitTimeout = NaiveConfig.DEFALUT_WAIT_TIMEOUT;

    /**
     * 写入日志的间隔.
     */
    public long logDuration = NaiveConfig.DEFAULT_LOG_DURATION;

    /**
     * 构造函数.
     *
     * @param srcProperty 连接属性.
     * @param tupleSelect 针对特定关系的查询语句.
     * @param whereScript 查询出关系的结构后的过滤条件.
     * @param metadataSql 查询关系metatdata的SQL语句.
     * @param attrinfoSql 查询关系属性的SQL语句.
     * @param isTemporary 是不是临时复制槽.
     * @param slotnameInf 复制槽名称信息.
     */
    private PgSnapshotConfig
        /* */( PgConnectionProperty srcProperty
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect
        /* */, PgSnapshotFilter whereScript
        /* */, String metadataSql
        /* */, String attrinfoSql
        /* */, boolean isTemporary
        /* */, String slotnameInf
        /* */)
    {
        this.srcProperty = srcProperty;
        this.tupleSelect = tupleSelect;
        this.whereScript = whereScript;
        this.metadataSql = metadataSql;
        this.attrinfoSql = attrinfoSql;
        this.isTemporary = isTemporary;
        this.slotnameInf = slotnameInf;
    }

    /**
     * 创建FORWARD_ONLY、READ_ONLY和CLOSE_CURSORS_AT_COMMIT的语句.
     *
     * @param c PostgreSQL数据库连接.
     * @return FORWARD_ONLY、READ_ONLY和CLOSE_CURSORS_AT_COMMIT的语句.
     * @throws SQLException 创建语句或者设置FORWARD TYPE抛出.
     */
    public Statement createStatement(Connection c) throws SQLException
    {
        Statement s = c.createStatement(ResultSet.TYPE_FORWARD_ONLY //
            , ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
        try {
            setStatement(s);
            return s;
        }
        catch (Exception ex) {
            s.close();
            throw ex;
        }
    }

    /**
     * 设置语句的FetchDirection和FetchSize.
     *
     * @param s 要设置的语句.
     * @throws SQLException 设置出现错误是抛出.
     */
    private void setStatement(Statement s) throws SQLException
    {
        s.setFetchDirection(ResultSet.FETCH_FORWARD);
        s.setFetchSize(this.rsFetchsize);
    }

    /**
     * 根据本配置查询关系列表.
     *
     * @param c 数据库连接.
     * @return 查询的关系列表.
     *
     * @throws SQLException 执行{@link #metadataSql}或者{@link #attrinfoSql}时出现错误时抛出.
     * @throws ScriptException 执行{@link #whereScript}出现错误时时抛出.
     */
    public ImmutableList<PgReplRelation> queryForRelations(Connection c)
        throws SQLException, ScriptException
    {
        List<PgReplRelationMetadata> metadata = new ArrayList<>();
        try (Statement stmt = this.createStatement(c);
             ResultSet rs = stmt.executeQuery(this.metadataSql)) {
            rs.setFetchDirection(ResultSet.FETCH_FORWARD);
            rs.setFetchSize(this.rsFetchsize);
            while (rs.next()) {
                rs.setFetchSize(this.rsFetchsize);
                long i = rs.getLong("relident");
                String s = rs.getString("dbschema");
                String r = rs.getString("relation");
                long h = rs.getLong("replchar");
                metadata.add(PgReplRelationMetadata.of(i, s, r, h));
            }
        }
        List<PgReplRelation> relalist = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(this.attrinfoSql //
            , ResultSet.TYPE_FORWARD_ONLY //
            , ResultSet.CONCUR_READ_ONLY //
            , ResultSet.CLOSE_CURSORS_AT_COMMIT)) {
            this.setStatement(ps);
            for (PgReplRelationMetadata r : metadata) {
                ps.setObject(1, r.relident);
                List<PgReplAttribute> attrlist = new ArrayList<>();
                try (ResultSet rs = ps.executeQuery()) {
                    rs.setFetchDirection(ResultSet.FETCH_FORWARD);
                    rs.setFetchSize(this.rsFetchsize);
                    while (rs.next()) {
                        rs.setFetchSize(this.rsFetchsize);
                        long f = rs.getLong("attflags");
                        String n = rs.getString("attrname");
                        long d = rs.getLong("datatype");
                        long m = rs.getLong("attypmod");
                        String s = rs.getString("tpschema");
                        String t = rs.getString("typename");
                        attrlist.add(PgReplAttribute.of(n, s, t, -1, f, d, m));
                    }
                }
                relalist.add(PgReplRelation.of(r, ImmutableList.copyOf(attrlist)));
            }
        }
        List<PgReplRelation> result = new ArrayList<>();
        for (PgReplRelation relation : relalist) {
            if (this.whereScript.eval(relation)) {
                result.add(relation);
            }
        }
        return ImmutableList.copyOf(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("rsFetchsize=");
        builder.append(rsFetchsize);
        builder.append(", whereScript=");
        builder.append(whereScript);
        for(Map.Entry<PgReplRelationName, String> e : tupleSelect.entrySet()) {
            builder.append(", tupleSelect[");
            builder.append(e.getKey());
            builder.append("]=");
            builder.append(e.getValue());
        }
        return builder.toString();
    }
}
