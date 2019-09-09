/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.method.SimpleMethodPstParams;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterDefault;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterScript;
import com.hktcode.pgstack.ruoshui.upper.pgsender.MainlineConfig;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgLockMode;
import com.hktcode.pgstack.ruoshui.upper.pgsender.SnapshotConfig;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UpperSnapshotPstParams implements SimpleMethodPstParams<UpcsmAction>
{
    public static UpperSnapshotPstParams of(JsonNode json)
        throws ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }

        JsonNode tupleSelectNode = json.path("tuple_select");
        Map<PgReplRelationName, String> map = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = tupleSelectNode.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            PgReplRelationName relationName = PgReplRelationName.ofTextString(e.getKey());
            map.put(relationName, e.getValue().asText());
        }
        ImmutableMap<PgReplRelationName, String> tupleSelect = ImmutableMap.copyOf(map);

        JsonNode whereScriptNode = json.path("where_script");
        PgSnapshotFilter whereScript;
        if (whereScriptNode.isMissingNode()) {
            whereScript = PgSnapshotFilterDefault.of();
        } else {
            whereScript = PgSnapshotFilterScript.of(whereScriptNode);
        }

        String relationSql = json.path("relation_sql").asText("");

        PgLockMode lockingMode;
        JsonNode lockingModeNode = json.path("locking_mode");
        if (lockingModeNode.isMissingNode()) {
            lockingMode = PgLockMode.NULL_LOCK;
        }
        else {
            lockingMode = PgLockMode.valueOf(lockingModeNode.asText());
        }
        int rsFetchsize = json.path("rs_fetchsize").asInt(-1);
        long waitTimeout = json.path("wait_timeout").asLong(-1);
        long logDuration = json.path("log_duration").asLong(-1);
        JsonNode srcPropertyNode = json.path("src_property");
        Map<String, String> srcMap = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> srcit = srcPropertyNode.fields();
        while (srcit.hasNext()) {
            Map.Entry<String, JsonNode> e = srcit.next();
            String key = e.getKey();
            srcMap.put(key, e.getValue().asText());
        }
        return new UpperSnapshotPstParams
            /* */( ImmutableMap.copyOf(srcMap)
            /* */, tupleSelect
            /* */, relationSql
            /* */, whereScript
            /* */, lockingMode
            /* */, rsFetchsize
            /* */, waitTimeout
            /* */, logDuration
            /* */);
    }

    public final ImmutableMap<String, String> srcProperty;

    public final ImmutableMap<PgReplRelationName, String> tupleSelect;

    public final String relationSql;

    public final PgSnapshotFilter whereScript;

    public final PgLockMode lockingMode;

    public final int rsFetchsize;

    public final long waitTimeout;

    public final long logDuration;

    @Override
    public SimpleMethodPstResult<UpcsmAction> run(UpcsmAction action)
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return action.pst(this);
    }

    public SnapshotConfig toConfig(MainlineConfig mainline)
    {
        if (mainline == null) {
            throw new ArgumentNullException("mainline");
        }
        Map<String, String> srcMap = new HashMap<>(mainline.srcProperty.propertyMap);
        for (Map.Entry<String, String> e : srcProperty.entrySet()) {
            srcMap.put(e.getKey(), e.getValue());
        }
        PgConnectionProperty src = PgConnectionProperty.of(ImmutableMap.copyOf(srcMap));

        Map<PgReplRelationName, String> tplMap = new HashMap<>(mainline.tupleSelect);
        for (Map.Entry<PgReplRelationName, String> e : tupleSelect.entrySet()) {
            tplMap.put(e.getKey(), e.getValue());
        }
        ImmutableMap<PgReplRelationName, String> tpl
            = ImmutableMap.copyOf(tplMap);

        String rel = "".equals(relationSql) ? mainline.relationSql : relationSql;

        PgSnapshotFilter whr = whereScript instanceof PgSnapshotFilterDefault ? mainline.whereScript : whereScript;

        PgLockMode lck = lockingMode == PgLockMode.NULL_LOCK ?
            mainline.lockingMode : lockingMode;

        SnapshotConfig result = SnapshotConfig.of(src, rel, whr, lck, mainline.logicalRepl, tpl);

        result.rsFetchsize = (rsFetchsize == -1 ? mainline.rsFetchsize : rsFetchsize);
        result.waitTimeout = (waitTimeout == -1 ? mainline.waitTimeout : waitTimeout);
        result.logDuration = (logDuration == -1 ? mainline.logDuration : logDuration);

        return result;
    }

    private UpperSnapshotPstParams
        /* */( ImmutableMap<String, String> srcProperty
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect
        /* */, String relationSql
        /* */, PgSnapshotFilter whereScript
        /* */, PgLockMode lockingMode
        /* */, int rsFetchsize
        /* */, long waitTimeout
        /* */, long logDuration
        /* */)
    {
        this.srcProperty = srcProperty;
        this.tupleSelect = tupleSelect;
        this.relationSql = relationSql;
        this.whereScript = whereScript;
        this.lockingMode = lockingMode;
        this.rsFetchsize = rsFetchsize;
        this.waitTimeout = waitTimeout;
        this.logDuration = logDuration;
    }
}
