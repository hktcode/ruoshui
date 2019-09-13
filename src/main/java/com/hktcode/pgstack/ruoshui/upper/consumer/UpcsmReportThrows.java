/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.hktcode.lang.exception.ArgumentNullException;

import java.io.IOException;

public class UpcsmReportThrows implements JsonSerializable
{
    public static UpcsmReportThrows of(long actionStart, Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpcsmReportThrows(actionStart, throwsError);
    }

    public final long actionStart;

    public final Throwable throwsError;

    private UpcsmReportThrows(long actionStart, Throwable throwsError)
    {
        this.actionStart = actionStart;
        this.throwsError = throwsError;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers)
        throws IOException
    {
        gen.writeStartObject();
        gen.writeObjectField("action_start", actionStart);
        String message = throwsError.getMessage();
        if (message != null) {
            gen.writeObjectField("throws_error", throwsError.getMessage());
        }
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
        throws IOException
    {
        this.serialize(gen, serializers);
    }
}
