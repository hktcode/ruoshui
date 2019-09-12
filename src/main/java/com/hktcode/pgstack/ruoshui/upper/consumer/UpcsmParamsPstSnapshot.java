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
import com.hktcode.pgstack.ruoshui.upper.pgsender.*;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UpcsmParamsPstSnapshot implements SimpleMethodPstParams<UpcsmAction>
{
    public static UpcsmParamsPstSnapshot of(JsonNode json)
        throws ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }

        JsonNode tupleSelectNode = json.path("tuple_select");
        ImmutableMap<PgReplRelationName, String> tupleSelect = PgConfig.toTupleSelect(tupleSelectNode);

        JsonNode whereScriptNode = json.path("where_script");
        PgFilter whereScript;
        if (whereScriptNode.isMissingNode()) {
            whereScript = PgFilterDefault.of();
        } else {
            whereScript = PgFilterScript.of(whereScriptNode);
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
        return new UpcsmParamsPstSnapshot
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

    private final ImmutableMap<String, String> srcProperty;

    private final ImmutableMap<PgReplRelationName, String> tupleSelect;

    private final String relationSql;

    private final PgFilter whereScript;

    private final PgLockMode lockingMode;

    private final int rsFetchsize;

    private final long waitTimeout;

    private final long logDuration;

    @Override
    public SimpleMethodPstResult<UpcsmAction> run(UpcsmAction action)
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return action.pst(this);
    }

    PgConfigSnapshot toConfig(PgConfigMainline mainline)
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

        PgFilter whr = whereScript instanceof PgFilterDefault ? mainline.whereScript : whereScript;

        PgLockMode lck = lockingMode == PgLockMode.NULL_LOCK ?
            mainline.lockingMode : lockingMode;

        PgConfigSnapshot result = PgConfigSnapshot.of(src, rel, whr, lck, mainline.logicalRepl, tpl, mainline.typelistSql);

        result.rsFetchsize = (rsFetchsize == -1 ? mainline.rsFetchsize : rsFetchsize);
        result.waitTimeout = (waitTimeout == -1 ? mainline.waitTimeout : waitTimeout);
        result.logDuration = (logDuration == -1 ? mainline.logDuration : logDuration);

        return result;
    }

    private UpcsmParamsPstSnapshot
        /* */( ImmutableMap<String, String> srcProperty
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect
        /* */, String relationSql
        /* */, PgFilter whereScript
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
