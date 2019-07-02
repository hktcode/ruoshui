/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgtriple.naive.NaiveConsumerConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterDefault;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterScript;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig.DEFAULT_ATTRINFO_SQL;
import static com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig.DEFAULT_RELATION_SQL;

public abstract class MainlineConfig extends NaiveConsumerConfig
{
    public static final String DEFAULT_METADATA_FORMAT = "" //
        + "\n WITH \"names\" as (select json_array_elements_text(?::json) as \"pubname\") " //
        + "\n SELECT                     \"t\".\"oid\"::int8 as \"relident\" " //
        + "\n      ,                       \"n\".\"nspname\" as \"dbschema\" " //
        + "\n      ,                       \"t\".\"relname\" as \"relation\" " //
        + "\n      ,   \"ascii\"(\"t\".\"relreplident\")::int8 as \"replchar\" " //
        + "\n FROM            \"names\"                       \"a\" " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_publication\" \"p\" " //
        + "\n              ON \"p\".\"pubname\" = \"a\".\"pubname\" " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_class\"       \"t\" " //
        + "\n              ON 1 = 1 " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_namespace\"   \"n\" " //
        + "\n              ON \"t\".\"relnamespace\" = \"n\".\"oid\" " //
        + "\n WHERE     \"t\".\"relpersistence\" = 'p'  " //
        + "\n       and \"t\".\"relkind\" in ('r', 'p')  " //
        + "\n       and \"n\".\"nspname\" not in ('information_schema', 'pg_catalog')  " //
        + "\n       and \"n\".\"nspname\" not like 'pg_temp%'  " //
        + "\n       and \"n\".\"nspname\" not like 'pg_toast%'  " //
        + "\n       and \"p\".\"puballtables\" " //
        + "\n UNION " //
        + "\n SELECT                     \"t\".\"oid\"::int8 as \"relident\" " //
        + "\n      ,                       \"n\".\"nspname\" as \"dbschema\" " //
        + "\n      ,                       \"t\".\"relname\" as \"relation\" " //
        + "\n      ,   \"ascii\"(\"t\".\"relreplident\")::int8 as \"replchar\" " //
        + "\n FROM            \"names\" \"a\" " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_publication\" \"p\" " //
        + "\n              ON \"p\".\"pubname\" = \"a\".\"pubname\" " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_publication_rel\" \"r\" " //
        + "\n              ON \"p\".\"oid\" = \"r\".\"prpubid\" " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_class\"       \"t\" " //
        + "\n              ON \"r\".\"prrelid\" = \"t\".\"oid\" " //
        + "\n      INNER JOIN \"pg_catalog\".\"pg_namespace\"   \"n\" " //
        + "\n              ON \"t\".\"relnamespace\" = \"n\".\"oid\" " //
        + "\n WHERE     \"t\".\"relpersistence\" = 'p'  " //
        + "\n       and \"t\".\"relkind\" in ('r', 'p')  " //
        + "\n       and \"n\".\"nspname\" not in ('information_schema', 'pg_catalog')  " //
        + "\n       and \"n\".\"nspname\" not like 'pg_temp%'  " //
        + "\n       and \"n\".\"nspname\" not like 'pg_toast%'  " //
        + "\n       and not \"p\".\"puballtables\" " //
        ;

    public static MainlineConfig ofJsonObject(JsonNode json) //
        throws ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode srcPropertyNode = json.path("src_property");
        PgConnectionProperty srcProperty = PgConnectionProperty.ofJsonObject(srcPropertyNode);

        JsonNode logicalReplNode = json.path("logical_repl");
        LogicalReplConfig logicalRepl = LogicalReplConfig.of(logicalReplNode);
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);

        JsonNode iniSnapshotNode = json.get("ini_snapshot");
        if (iniSnapshotNode == null) {
            return MainlineConfigTxaction.of(srcProperty, logicalRepl, waitTimeout, logDuration);
        }
        JsonNode tupleSelectNode = iniSnapshotNode.path("tuple_select");
        JsonNode whereScriptNode = iniSnapshotNode.path("where_script");
        int r = iniSnapshotNode.path("rs_fetchsize").asInt(PgSnapshotConfig.DEFAULT_RS_FETCHISIZE);
        String m = iniSnapshotNode.path("metadata_sql").asText(DEFAULT_RELATION_SQL);
        String a = iniSnapshotNode.path("attrinfo_sql").asText(DEFAULT_ATTRINFO_SQL);
        Map<PgReplRelationName, String> map = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = tupleSelectNode.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            PgReplRelationName relationName = PgReplRelationName.ofTextString(e.getKey());
            map.put(relationName, e.getValue().asText());
        }
        ImmutableMap<PgReplRelationName, String> t = ImmutableMap.copyOf(map);
        PgSnapshotFilter w;
        if (whereScriptNode.isMissingNode()) {
            w = PgSnapshotFilterDefault.of();
        }
        else {
            w = PgSnapshotFilterScript.of(whereScriptNode);
        }
        PgSnapshotConfig iniSnapshot = PgSnapshotConfig.of(srcProperty, t, w, m, a, false, logicalRepl.slotName);
        iniSnapshot.waitTimeout = waitTimeout;
        iniSnapshot.logDuration = logDuration;
        iniSnapshot.rsFetchsize = r;
        return MainlineConfigSnapshot.of(srcProperty, logicalRepl, iniSnapshot, waitTimeout, logDuration);
    }

    public final PgConnectionProperty srcProperty;

    public final LogicalReplConfig logicalRepl;

    MainlineConfig //
        /* */( PgConnectionProperty srcProperty //
        /* */, LogicalReplConfig logicalRepl //
        /* */, long waitTimeout //
        /* */, long logDuration //
        /* */)
    {
        super(waitTimeout, logDuration);
        this.srcProperty = srcProperty;
        this.logicalRepl = logicalRepl;
    }
}
