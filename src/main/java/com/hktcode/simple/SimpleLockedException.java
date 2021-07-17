package com.hktcode.simple;

import com.hktcode.jackson.HttpStatusJacksonObjectException;
import com.hktcode.jackson.JacksonObjectException;
import org.springframework.http.HttpStatus;

public class SimpleLockedException extends HttpStatusJacksonObjectException
{
    public static final HttpStatus CODE = HttpStatus.LOCKED;

    public SimpleLockedException()
    {
        super(CODE);
    }

    // TODO ...
}
