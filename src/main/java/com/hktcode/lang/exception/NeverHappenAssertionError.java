/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.lang.exception;

public class NeverHappenAssertionError extends AssertionError
{
    public NeverHappenAssertionError()
    {
        super("");
    }

    public NeverHappenAssertionError(String message)
    {
        super(message);
    }

    public NeverHappenAssertionError(String message, Throwable cause)
    {
        super(message, cause);
        if (cause == null) {
            throw new ArgumentNullException("cause");
        }
    }

    public NeverHappenAssertionError(Throwable cause)
    {
        super("should not throw throwable", cause);
        if (cause == null) {
            throw new ArgumentNullException("cause");
        }
    }
}
