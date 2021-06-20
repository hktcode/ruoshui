package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.simple.SimpleWkstepArgval;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpjctWkstepArgval extends SimpleWkstepArgval
{
    public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UpjctWkstepArgval.class, "UpjctArgval.yml");

    public static UpjctWkstepArgval ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        long waitTimeout = json.path("wait_timeout").asLong(SimpleWkstepArgval.DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(SimpleWkstepArgval.DEFAULT_LOG_DURATION);
        return new UpjctWkstepArgval(waitTimeout, logDuration);
    }

    private UpjctWkstepArgval(long waitTimeout, long logDuration)
    {
        super();
        this.waitTimeout = waitTimeout;
        this.logDuration = logDuration;
    }

    public UpjctWkstepAction action()
    {
        return UpjctWkstepAction.of();
    }
}
