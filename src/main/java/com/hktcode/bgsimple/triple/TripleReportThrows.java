/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.hktcode.lang.exception.ArgumentNullException;

import java.io.IOException;

public class TripleReportThrows implements JsonSerializable
{
    public static TripleReportThrows of(long actionStart, Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new TripleReportThrows(actionStart, throwsError);
    }

    public final long actionStart;

    public final Throwable throwsError;

    private TripleReportThrows(long actionStart, Throwable throwsError)
    {
        this.actionStart = actionStart;
        this.throwsError = throwsError;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) //
        throws IOException
    {
        gen.writeStartObject();
        gen.writeNumberField("action_start", actionStart);
        String message = throwsError.getMessage();
        message = (message == null ? "" : message);
        if ("".equals(message)) {
            message = "throws " + throwsError.getClass().getName();
        }
        gen.writeStringField("throws_error", message);
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType //
        /* */( JsonGenerator gen //
        /* */, SerializerProvider serializers //
        /* */, TypeSerializer typeSer //
        /* */) throws IOException
    {
        if (gen == null) {
            throw new ArgumentNullException("gen");
        }
        if (serializers == null) {
            throw new ArgumentNullException("serializers");
        }
        if (typeSer == null) {
            throw new ArgumentNullException("typeSer");
        }
        serialize(gen, serializers);
    }
}
