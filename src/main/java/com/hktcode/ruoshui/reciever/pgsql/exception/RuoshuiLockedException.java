package com.hktcode.ruoshui.reciever.pgsql.exception;

import com.hktcode.jackson.JacksonObjectException;
import org.springframework.http.HttpStatus;

public class RuoshuiLockedException extends JacksonObjectException
{
    public static final HttpStatus CODE = HttpStatus.LOCKED;

    public RuoshuiLockedException()
    {
        super(CODE);
    }

    // TODO ...
}
