/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterDefault;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterScript;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.hktcode.bgsimple.triple.TripleConfig.DEFALUT_WAIT_TIMEOUT;
import static com.hktcode.bgsimple.triple.TripleConfig.DEFAULT_LOG_DURATION;

public class MainlineConfig extends PgSnapshotConfig
{
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
        int rsFetchsize = json.path("rs_fetchsize").asInt(DEFAULT_RS_FETCHSIZE);
        boolean getSnapshot = json.path("get_snapshot").asBoolean(true);
        result.waitTimeout = waitTimeout;
        result.logDuration = logDuration;
        result.rsFetchsize = rsFetchsize;
        result.getSnapshot = getSnapshot;
        return result;
    }

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

    public final String typelistSql;

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
        super(srcProperty, relationSql, whereScript, lockingMode, logicalRepl, tupleSelect);
        this.typelistSql = typelistSql;
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
