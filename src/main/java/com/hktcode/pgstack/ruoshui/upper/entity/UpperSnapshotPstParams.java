/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgsimple.SimpleBasicPstBgParams;
import com.hktcode.bgsimple.SimpleBasicPstBgResult;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterDefault;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterScript;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;

import javax.script.ScriptException;
import java.time.ZonedDateTime;

public class UpperSnapshotPstParams
    implements SimpleBasicPstBgParams<UpperConsumer>
{
    public static UpperSnapshotPstParams of(JsonNode json)
        throws ScriptException
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode whereScriptNode = json.path("where_script");
        PgSnapshotFilter w;
        if (whereScriptNode.isMissingNode()) {
            w = PgSnapshotFilterDefault.of();
        }
        else {
            w = PgSnapshotFilterScript.of(whereScriptNode);
        }

        return new UpperSnapshotPstParams(json, w);
    }

    public SimpleBasicPstBgResult<UpperConsumer> run(UpperConsumer worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.pstWithSnapshot(this.json, this.whereScript);
    }

    private final JsonNode json;

    private final PgSnapshotFilter whereScript;

    @Override
    public SimpleBasicPstBgResult<UpperConsumer> //
    run(UpperConsumer worker, Throwable reasons, ZonedDateTime endtime)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return worker.pst(reasons, endtime);
    }

    private UpperSnapshotPstParams(JsonNode json, PgSnapshotFilter whereScript)
    {
        this.json = json;
        this.whereScript = whereScript;
    }
}
