/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalReplConfig;
import com.hktcode.pgstack.ruoshui.pgsql.PgConnectionProperty;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplRelationName;

import javax.script.ScriptException;
import java.sql.Statement;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.hktcode.bgsimple.triple.TripleConfig.DEFALUT_WAIT_TIMEOUT;
import static com.hktcode.bgsimple.triple.TripleConfig.DEFAULT_LOG_DURATION;

public class PgConfigMainline extends PgConfig
{
    public static PgConfigMainline ofJsonObject(JsonNode json) //
        throws ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode srcPropertyNode = json.path("src_property");
        PgConnectionProperty srcProperty = PgConnectionProperty.ofJsonObject(srcPropertyNode);

        String typelistSql = json.path("typelist_sql").asText(DEFAULT_TYPELIST_SQL);
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
        ImmutableMap<PgReplRelationName, String> tupleSelect = PgConfig.toTupleSelect(tupleSelectNode);
        PgFilter whereScript;
        if (whereScriptNode.isMissingNode()) {
            whereScript = PgFilterDefault.of();
        } else {
            whereScript = PgFilterScript.of(whereScriptNode);
        }
        String lockingModeText = json.path("locking_mode").asText("SHARE_UPDATE_EXCLUSIVE");
        PgLockMode lockingMode = PgLockMode.valueOf(lockingModeText);
        boolean getSnapshot = json.path("get_snapshot").asBoolean(true);
        PgConfigMainline result = new PgConfigMainline
                /* */( srcProperty //
                /* */, typelistSql //
                /* */, relationSql //
                /* */, whereScript //
                /* */, lockingMode //
                /* */, logicalRepl //
                /* */, tupleSelect //
                /* */, getSnapshot //
                /* */);
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        int rsFetchsize = json.path("rs_fetchsize").asInt(DEFAULT_RS_FETCHSIZE);
        result.waitTimeout = waitTimeout;
        result.logDuration = logDuration;
        result.rsFetchsize = rsFetchsize;
        return result;
    }

    private PgConfigMainline
        /* */( PgConnectionProperty srcProperty //
        /* */, String typelistSql //
        /* */, String relationSql //
        /* */, PgFilter whereScript //
        /* */, PgLockMode lockingMode //
        /* */, LogicalReplConfig logicalRepl //
        /* */, ImmutableMap<PgReplRelationName, String> tupleSelect //
        /* */, boolean getSnapshot //
        /* */) //
    {
        super(srcProperty, relationSql, whereScript, lockingMode, logicalRepl, tupleSelect, typelistSql, getSnapshot);
    }

    @Override
    public PgAction afterSnapshot(PgActionDataSsFinish action)
    {
        return PgActionDataTypelistSnapshot.of(action);
    }

    @Override
    public PgDeputeCreateSlotMainline newCreateSlot(Statement statement)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        return PgDeputeCreateSlotMainline.of(statement, logicalRepl.slotName);
    }

    @Override
    public PgAction createsAction(AtomicReference<SimpleStatus> status, TransferQueue<PgRecord> tqueue)
    {
        if (this.getSnapshot) {
            return PgActionDataRelaList.of(this, status, tqueue);
        } else {
            return PgActionDataTypelistStraight.of(this, status, tqueue);
        }
    }
}
