package com.hktcode.jackson;

import org.springframework.http.HttpStatus;

public abstract class HttpStatusJacksonObjectException extends JacksonObjectException
{
    public final HttpStatus code;

    protected HttpStatusJacksonObjectException(HttpStatus code)
    {
        super();
        this.code = code;
    }

    protected HttpStatusJacksonObjectException(HttpStatus code, String message)
    {
        super(message);
        this.code = code;
    }

    protected HttpStatusJacksonObjectException(HttpStatus code, Throwable cause)
    {
        super(cause);
        this.code = code;
    }

    protected HttpStatusJacksonObjectException(HttpStatus code, String message, Throwable cause)
    {
        super(message, cause);
        this.code = code;
    }

    protected HttpStatusJacksonObjectException(HttpStatus code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
}
