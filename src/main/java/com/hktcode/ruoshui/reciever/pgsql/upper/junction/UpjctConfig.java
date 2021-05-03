package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.simple.SimpleConfig;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpjctConfig extends SimpleConfig
{
    public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UpjctConfig.class, "UpjctConfig.yml");

    public static UpjctConfig ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        long waitTimeout = json.path("wait_timeout").asLong(SimpleConfig.DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(SimpleConfig.DEFAULT_LOG_DURATION);
        return new UpjctConfig(waitTimeout, logDuration);
    }

    private UpjctConfig(long waitTimeout, long logDuration)
    {
        super();
        this.waitTimeout = waitTimeout;
        this.logDuration = logDuration;
    }

    public UpjctWorker worker(UpperExesvc exesvc)
    {
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        return UpjctWorker.of(this, UpjctMeters.of(), exesvc);
    }
}
