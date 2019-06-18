/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.io.IOException;

public class LogSequenceNumberJacksonSerializer //
    extends JsonSerializer<LogSequenceNumber>
{
    public static LogSequenceNumberJacksonSerializer of()
    {
        return new LogSequenceNumberJacksonSerializer();
    }

    private LogSequenceNumberJacksonSerializer()
    {
    }

    @Override
    public void serialize //
        /* */( LogSequenceNumber value //
        /* */, JsonGenerator gen //
        /* */, SerializerProvider serializers //
        /* */) throws IOException //
    {
        if (value == null) {
            throw new ArgumentNullException("value");
        }
        if (gen == null) {
            throw new ArgumentNullException("gen");
        }
        gen.writeString(value.asString());
    }
}
