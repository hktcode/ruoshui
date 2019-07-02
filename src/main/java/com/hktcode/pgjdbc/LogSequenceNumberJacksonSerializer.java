/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.io.IOException;

/**
 * the Jackson Serializer for {@link LogSequenceNumber}.
 */
public class LogSequenceNumberJacksonSerializer //
    extends JsonSerializer<LogSequenceNumber>
{
    /**
     * Obtain a {@link LogSequenceNumberJacksonSerializer}.
     *
     * @return a {@link LogSequenceNumberJacksonSerializer} object.
     */
    public static LogSequenceNumberJacksonSerializer of()
    {
        return new LogSequenceNumberJacksonSerializer();
    }

    /**
     * contructor.
     */
    private LogSequenceNumberJacksonSerializer()
    {
    }

    /**
     * writes the {@code value} into {@code gen} as String.
     *
     * @param value the special {@link LogSequenceNumber} object.
     * @param gen write into the gen object.
     * @param serializers serializers obtain.
     *
     * @throws IOException TODO: when will throws the IOException.
     * @throws ArgumentNullException if {@code value} or {@code gen} is {@code null}.
     */
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
