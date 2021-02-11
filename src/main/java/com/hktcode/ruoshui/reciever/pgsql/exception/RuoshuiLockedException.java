package com.hktcode.ruoshui.reciever.pgsql.exception;

import com.hktcode.jackson.HttpStatusJacksonObjectException;
import com.hktcode.jackson.JacksonObjectException;
import org.springframework.http.HttpStatus;

public class RuoshuiLockedException extends HttpStatusJacksonObjectException
{
    public static final HttpStatus CODE = HttpStatus.LOCKED;

    public RuoshuiLockedException()
    {
        super(CODE);
    }

    // TODO ...
}
