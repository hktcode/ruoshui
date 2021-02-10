/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalReplConfig;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgConnectionProperty;

public class UpcsmConfig extends TqueueConfig
{
    public static UpcsmConfig ofJsonObject(JsonNode json) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        JsonNode srcPropertyNode = json.path("src_property");
        PgConnectionProperty srcProperty = PgConnectionProperty.ofJsonObject(srcPropertyNode);

        JsonNode logicalReplNode = json.path("logical_repl");
        LogicalReplConfig logicalRepl = LogicalReplConfig.of(logicalReplNode);

        UpcsmConfig result = new UpcsmConfig(srcProperty, logicalRepl);
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        result.waitTimeout = waitTimeout;
        result.logDuration = logDuration;
        return result;
    }

    public final PgConnectionProperty srcProperty;

    public final LogicalReplConfig logicalRepl;

    protected UpcsmConfig(PgConnectionProperty srcProperty, LogicalReplConfig logicalRepl)
    {
        this.srcProperty = srcProperty;
        this.logicalRepl = logicalRepl;
    }
}
