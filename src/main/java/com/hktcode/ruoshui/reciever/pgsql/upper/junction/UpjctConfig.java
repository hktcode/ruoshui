package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.simple.SimpleConfig;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpjctConfig extends SimpleConfig<UpjctConfig, UpjctMetric, UpperHolder>
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

    @Override
    public UpjctActionRun put(UpjctMetric metric, UpperHolder entity)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (entity == null) {
            throw new ArgumentNullException("entity");
        }
        return UpjctActionRun.of(this, metric, entity);
    }
}
