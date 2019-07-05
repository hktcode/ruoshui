/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgmethod.BgMethodPstParams;
import com.hktcode.bgmethod.BgMethodPstResult;
import com.hktcode.bgsimple.method.SimpleMethodPstParams;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilter;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterDefault;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotFilterScript;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;

import javax.script.ScriptException;
import java.time.ZonedDateTime;

public class UpperSnapshotPstParams
    implements SimpleMethodPstParams<UpperConsumer, UpperConsumerMetric>
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

    @Override
    public SimpleMethodPstResult<UpperConsumer, UpperConsumerMetric> run(UpperConsumer worker, UpperConsumerMetric metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.pstWithSnapshot(this.json, this.whereScript, metric);
    }

    private final JsonNode json;

    private final PgSnapshotFilter whereScript;

    private UpperSnapshotPstParams(JsonNode json, PgSnapshotFilter whereScript)
    {
        this.json = json;
        this.whereScript = whereScript;
    }
}
