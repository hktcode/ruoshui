/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.triple.TripleConsumerConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterDefault;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterScript;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainlineConfig extends TripleConsumerConfig
{
    private static final Logger logger = LoggerFactory.getLogger(MainlineConfig.class);

    private static final String TYPELIST_SQL = "" //
        + "\n select \"t\".    \"oid\"::int8 as \"datatype\" " //
        + "\n      , \"n\".\"nspname\"::text as \"tpschema\" " //
        + "\n      , \"t\".\"typname\"::text as \"typename\" " //
        + "\n  from            \"pg_catalog\".\"pg_type\"      \"t\" " //
        + "\n       inner join \"pg_catalog\".\"pg_namespace\" \"n\" " //
        + "\n               on \"t\".\"typnamespace\" = \"n\".\"oid\" " //
        + "\n ";

    public static MainlineConfig ofJsonObject(JsonNode json) //
        throws ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode srcPropertyNode = json.path("src_property");
        PgConnectionProperty srcProperty = PgConnectionProperty.ofJsonObject(srcPropertyNode);

        String typelistSql = json.path("typelist_sql").asText(TYPELIST_SQL);
        JsonNode logicalReplNode = json.path("logical_repl");
        LogicalReplConfig logicalRepl = LogicalReplConfig.of(logicalReplNode);

        // String initialStep = json.path("initial_step").asText("snapshot");
        // snapshot
        // create_slot_and_publication
        // create_slot
        // create_publication

        JsonNode tupleSelectNode = json.path("tuple_select");
        JsonNode whereScriptNode = json.path("where_script");
        String relationSql = json.path("relation_sql").asText(DEFAULT_RELATION_SQL);
        Map<PgReplRelationName, String> map = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = tupleSelectNode.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            PgReplRelationName relationName = PgReplRelationName.ofTextString(e.getKey());
            map.put(relationName, e.getValue().asText());
        }
        ImmutableMap<PgReplRelationName, String> tupleSelect = ImmutableMap.copyOf(map);
        PgSnapshotFilter whereScript;
        if (whereScriptNode.isMissingNode()) {
            whereScript = PgSnapshotFilterDefault.of();
        } else {
            whereScript = PgSnapshotFilterScript.of(whereScriptNode);
        }
        String lockingModeText = json.path("locking_mode").asText("SHARE_UPDATE_EXCLUSIVE");
        PgLockMode lockingMode = PgLockMode.valueOf(lockingModeText);
        MainlineConfig result = MainlineConfig.of
            /* */( srcProperty //
                /* */, typelistSql //
                /* */, relationSql //
                /* */, whereScript //
                /* */, lockingMode //
                /* */, logicalRepl //
                /* */, tupleSelect //
                /* */);
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        int rsFetchsize = json.path("rs_fetchsize").asInt(DEFAULT_RS_FETCHISIZE);
        boolean getSnapshot = json.path("get_snapshot").asBoolean(true);
        result.waitTimeout = waitTimeout;
        result.logDuration = logDuration;
        result.rsFetchsize = rsFetchsize;
        result.getSnapshot = getSnapshot;
        return result;
    }

    /**
     * 默认的{@link ResultSet#setFetchSize(int)}值.
     */
    public static final int DEFAULT_RS_FETCHISIZE = 128;

    static MainlineConfig of //
        /* */( PgConnectionProperty srcProperty //
        /* */, String typelistSql //
        /* */, String relationSql //
        /* */, PgSnapshotFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect
        /* */) //
    {
        if (srcProperty == null) {
            throw new ArgumentNullException("srcProperty");
        }
        if (typelistSql == null) {
            throw new ArgumentNullException("typelistSql");
        }
        if (relationSql == null) {
            throw new ArgumentNullException("relationSql");
        }
        if (whereScript == null) {
            throw new ArgumentNullException("whereScript");
        }
        if (lockingMode == null) {
            throw new ArgumentNullException("lockingMode");
        }
        if (logicalRepl == null) {
            throw new ArgumentNullException("logicalRepl");
        }
        if (tupleSelect == null) {
            throw new ArgumentNullException("tupleSelect");
        }
        return new MainlineConfig(srcProperty, typelistSql, relationSql, whereScript, lockingMode, logicalRepl, tupleSelect);
    }

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

    public PreparedStatement queryTupleval(PgConnection pgdata, PgReplRelation relation)
        throws SQLException
    {
        PgReplRelationName name = PgReplRelationName.of(relation.dbschema, relation.relation);
        String sql = this.tupleSelect.get(name);
        if (sql == null) {
            sql = buildSelect(pgdata, relation);
        }
        PreparedStatement ps = pgdata.prepareStatement
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

    static String buildSelect(PgConnection c, PgReplRelation r) //
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

    public PreparedStatement queryRelalist(Connection connection)
        throws SQLException
    {
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (String name : this.logicalRepl.publicationNames) {
            arrayNode.add(name);
        }
        PreparedStatement ps = connection.prepareStatement
            /* */( relationSql //
                /* */, ResultSet.TYPE_FORWARD_ONLY //
                /* */, ResultSet.CONCUR_READ_ONLY //
                /* */, ResultSet.CLOSE_CURSORS_AT_COMMIT //
                /* */);
        try {
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);
            ps.setFetchSize(this.rsFetchsize);
            ps.setString(1, arrayNode.toString());
            logger.info("prepared relalist statement: parameter={}, sql={}", arrayNode, relationSql);
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

    public final ImmutableMap<PgReplRelationName, String> tupleSelect;

    public final String relationSql;

    public final PgSnapshotFilter whereScript;

    public final PgLockMode lockingMode;

    public static final int DEFAULT_RS_FETCHSIZE = 10240;

    public final String typelistSql;

    public final PgConnectionProperty srcProperty;

    public final LogicalReplConfig logicalRepl;

    public int rsFetchsize = DEFAULT_RS_FETCHSIZE;

    public boolean getSnapshot = true;

    MainlineConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, String typelistSql //
        /* */, String relationSql //
        /* */, PgSnapshotFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect
        /* */) //
    {
        this.relationSql = relationSql;
        this.whereScript = whereScript;
        this.lockingMode = lockingMode;
        this.tupleSelect = tupleSelect;
        this.srcProperty = srcProperty;
        this.logicalRepl = logicalRepl;
        this.typelistSql = typelistSql;
    }

    public String lockRelation(PgReplRelation relation, PgConnection cnt)
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

    public PreparedStatement queryTypelist(PgConnection pgdata)
        throws SQLException
    {
        PreparedStatement ps = pgdata.prepareStatement
            /* */(TYPELIST_SQL //
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
}
