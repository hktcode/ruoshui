/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.sql.*;
import java.util.concurrent.Callable;

public abstract class PgsenderConfig extends TqueueConfig
{
    /**
     * 默认的{@link ResultSet#setFetchSize(int)}值.
     */
    public static final int DEFAULT_RS_FETCHSIZE = 10240;

    public static final String DEFAULT_RELATION_SQL = "" //
        + "\n WITH \"publication\" as " //
        + "\n ( select \"p\".\"puballtables\" " //
        + "\n        , \"p\".\"oid\" " //
        + "\n   from            \"pg_catalog\".\"pg_publication\" p " //
        + "\n  WHERE ?::jsonb ?? p.pubname " //
        + "\n ) " //
        + "\n SELECT                                            \"t\".\"oid\"::int8 as \"relident\" " //
        + "\n      ,                                        \"n\".\"nspname\"::text as \"dbschema\" " //
        + "\n      ,                                        \"t\".\"relname\"::text as \"relation\" " //
        + "\n      ,                        \"ascii\"(\"t\".\"relreplident\")::int8 as \"replchar\" " //
        + "\n      , (case when \"k\".\"conrelid\" is null then 0 else 1 end)::int8 as \"attflags\" " //
        + "\n      ,                                              \"a\".\"attname\" as \"attrname\" " //
        + "\n      ,                                       \"a\".\"atttypid\"::int8 as \"datatype\" " //
        + "\n      ,                                      \"a\".\"atttypmod\"::int8 as \"attypmod\" " //
        + "\n      ,                                             \"tn\".\"nspname\" as \"tpschema\" " //
        + "\n      ,                                              \"y\".\"typname\" as \"typename\" " //
        + "\n FROM            ( SELECT \"t\".\"oid\" " //
        + "\n                        , \"t\".\"relname\" " //
        + "\n                        , \"t\".\"relnamespace\" " //
        + "\n                        , \"t\".\"relreplident\" " //
        + "\n                        , \"t\".\"relpersistence\" " //
        + "\n                        , \"t\".\"relkind\" " //
        + "\n                   FROM  \"pg_catalog\".\"pg_class\" \"t\" " //
        + "\n                   WHERE EXISTS (SELECT * FROM \"publication\" WHERE puballtables) " //
        + "\n                   UNION " //
        + "\n                   SELECT \"t\".\"oid\" " //
        + "\n                        , \"t\".\"relname\" " //
        + "\n                        , \"t\".\"relnamespace\" " //
        + "\n                        , \"t\".\"relreplident\" " //
        + "\n                        , \"t\".\"relpersistence\" " //
        + "\n                        , \"t\".\"relkind\" " //
        + "\n                   FROM            \"publication\" \"a\" " //
        + "\n                        INNER JOIN \"pg_catalog\".\"pg_publication_rel\" \"r\" " //
        + "\n                                ON \"a\".\"oid\" = \"r\".\"prpubid\" " //
        + "\n                        INNER JOIN \"pg_catalog\".\"pg_class\"       \"t\" " //
        + "\n                                ON \"r\".\"prrelid\" = \"t\".\"oid\" " //
        + "\n                   WHERE     NOT EXISTS (SELECT * FROM \"publication\" WHERE puballtables) " //
        + "\n                         and NOT \"a\".\"puballtables\" " //
        + "\n                 ) t " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_namespace\" \"n\" " //
        + "\n              ON \"t\".\"relnamespace\" = \"n\".\"oid\" " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_attribute\" a " //
        + "\n              ON \"t\".\"oid\" = \"a\".\"attrelid\" " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_type\" y " //
        + "\n              ON \"a\".\"atttypid\" = \"y\".\"oid\" " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_namespace\" tn " //
        + "\n              ON \"y\".\"typnamespace\" = tn.\"oid\" " //
        + "\n       LEFT JOIN ( select     \"c\".\"conrelid\" as \"conrelid\" " //
        + "\n                        , \"unnest\"(\"conkey\") as \"conkey\" " //
        + "\n                   from            \"pg_catalog\".\"pg_constraint\" c " //
        + "\n                        inner join ( select \"c\".\"conrelid\" as \"conrelid\" " //
        + "\n                                          , \"min\"(\"c\".\"oid\") as \"oid\" " //
        + "\n                                     from            \"pg_catalog\".\"pg_constraint\" \"c\" " //
        + "\n                                          inner join ( select \"conrelid\" as \"conrelid\" " //
        + "\n                                                            , max(\"contype\") as \"contype\" " //
        + "\n                                                       from \"pg_catalog\".\"pg_constraint\" " //
        + "\n                                                       where     \"contype\" in ('p', 'u') " //
        + "\n                                                             and \"conrelid\" <> 0 " //
        + "\n                                                       group by \"conrelid\" " //
        + "\n                                                     ) m " //
        + "\n                                                  on     m.\"conrelid\" = c.\"conrelid\" " //
        + "\n                                                     and m.\"contype\" = c.\"contype\" " //
        + "\n                                     group by c.\"conrelid\" " //
        + "\n                                   ) m " //
        + "\n                                on m.\"oid\" = c.\"oid\" " //
        + "\n                 ) k " //
        + "\n              on     a.\"attrelid\" = k.\"conrelid\" " //
        + "\n                 and a.\"attnum\" = k.\"conkey\" " //
        + "\n WHERE     \"t\".\"relpersistence\" = 'p' " //
        + "\n       and \"t\".\"relkind\" in ('r', 'p') " //
        + "\n       and \"n\".\"nspname\" not in ('information_schema', 'pg_catalog') " //
        + "\n       and \"n\".\"nspname\" not like 'pg_temp%' " //
        + "\n       and \"n\".\"nspname\" not like 'pg_toast%' " //
        + "\n       and not \"a\".\"attisdropped\" " //
        + "\n       and \"a\".\"attnum\" > 0 " //
        + "\n ORDER BY \"t\".\"oid\", \"a\".\"attnum\" " //
        + "";

    public static final String DEFAULT_TYPELIST_SQL = "" //
        + "\n select \"t\".    \"oid\"::int8 as \"datatype\" " //
        + "\n      , \"n\".\"nspname\"::text as \"tpschema\" " //
        + "\n      , \"t\".\"typname\"::text as \"typename\" " //
        + "\n  from            \"pg_catalog\".\"pg_type\"      \"t\" " //
        + "\n       inner join \"pg_catalog\".\"pg_namespace\" \"n\" " //
        + "\n               on \"t\".\"typnamespace\" = \"n\".\"oid\" " //
        + "\n ";

    public PreparedStatement queryTypelist(PgConnection pgdata)
        throws SQLException
    {
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        return preparedStatement(pgdata, typelistSql);
    }

    public PreparedStatement queryTupleval(PgConnection pgdata, PgReplRelation relation)
        throws SQLException
    {
        PgReplRelationName name = PgReplRelationName.of(relation.dbschema, relation.relation);
        String sql = this.tupleSelect.get(name);
        if (sql == null) {
            sql = buildSelect(pgdata, relation);
        }
        return preparedStatement(pgdata, sql);
    }

    private PreparedStatement preparedStatement(PgConnection pg, String sql) //
        throws SQLException
    {
        if (pg == null) {
            throw new ArgumentNullException("pg");
        }
        if (sql == null) {
            throw new ArgumentNullException("sql");
        }
        PreparedStatement ps = pg.prepareStatement //
                /* */( sql //
                /* */, ResultSet.TYPE_FORWARD_ONLY //
                /* */, ResultSet.CONCUR_READ_ONLY //
                /* */, ResultSet.CLOSE_CURSORS_AT_COMMIT //
                /* */);
        try {
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);
            ps.setFetchSize(this.rsFetchsize);
            return ps;
        }
        catch (Exception ex) {
            ps.close();
            throw ex;
        }
    }

    private static String buildSelect(PgConnection c, PgReplRelation r) //
        throws SQLException
    {
        StringBuilder sb = new StringBuilder("\nSELECT ");
        String n = c.escapeIdentifier(r.attrlist.get(0).attrname);
        sb.append(n);
        sb.append("::text as ");
        sb.append(n);
        for(int i = 1; i < r.attrlist.size(); ++i) {
            sb.append("\n     , ");
            n = c.escapeIdentifier(r.attrlist.get(i).attrname);
            sb.append(n);
            sb.append("::text as ");
            sb.append(n);
        }
        sb.append("\nFROM ");
        sb.append(c.escapeIdentifier(r.dbschema));
        sb.append(".");
        sb.append(c.escapeIdentifier(r.relation));
        return sb.toString();
    }

    public PreparedStatement queryRelalist(PgConnection pgdata) //
        throws SQLException
    {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (String name : this.logicalRepl.publicationNames) {
            arrayNode.add(name);
        }
        PreparedStatement ps = this.preparedStatement(pgdata, relationSql);
        try {
            ps.setString(1, arrayNode.toString());
            return ps;
        }
        catch (Exception ex) {
            ps.close();
            throw ex;
        }
    }

    public boolean whereRelalist(PgReplRelation relation) throws ScriptException
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return whereScript.eval(relation);
    }

    public String lockStatement(PgReplRelation relation, PgConnection cnt)
        throws SQLException
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return "LOCK TABLE ONLY " //
            + cnt.escapeIdentifier(relation.dbschema) //
            + "." //
            + cnt.escapeIdentifier(relation.relation) //
            + " IN " //
            + lockingMode.textFormat //
            + " MODE" //
            ;
    }

    public final PgConnectionProperty srcProperty;

    public final ImmutableMap<PgReplRelationName, String> tupleSelect;

    public final String relationSql;

    public final PgSnapshotFilter whereScript;

    public final PgLockMode lockingMode;

    public final LogicalReplConfig logicalRepl;

    public int rsFetchsize = DEFAULT_RS_FETCHSIZE;

    public final String typelistSql;

    public final boolean getSnapshot;

    protected PgsenderConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, String relationSql //
        /* */, PgSnapshotFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */) //
    {
        this.srcProperty = srcProperty;
        this.relationSql = relationSql;
        this.whereScript = whereScript;
        this.lockingMode = lockingMode;
        this.tupleSelect = tupleSelect;
        this.logicalRepl = logicalRepl;
        this.typelistSql = DEFAULT_TYPELIST_SQL;
        this.getSnapshot = true;
    }

    protected PgsenderConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, String relationSql //
        /* */, PgSnapshotFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */, String typelistSql //
        /* */, boolean getSnapshot //
        /* */) //
    {
        this.srcProperty = srcProperty;
        this.relationSql = relationSql;
        this.whereScript = whereScript;
        this.lockingMode = lockingMode;
        this.tupleSelect = tupleSelect;
        this.logicalRepl = logicalRepl;
        this.typelistSql = typelistSql;
        this.getSnapshot = getSnapshot;
    }

    public PgAction afterSnapshot(PgActionDataSsFinish action)
    {
        return PgActionTerminateEnd.of(action);
    }

    public PgRecordLogicalMsg createMessage(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return PgRecordLogicalMsg.of(lsn, msg);
    }

    public abstract Callable<PgReplSlotTuple> newCreateSlot(Statement statement);

    public PgRecordPauseWorld pauseWorldMsg()
    {
        return null; // TODO
    }

    public PgRecordCreateSlot createSlotMsg(PgReplSlotTuple tuple)
    {
        if (tuple == null) {
            throw new ArgumentNullException("tuple");
        }
        return null; // TODO:
    }
}
