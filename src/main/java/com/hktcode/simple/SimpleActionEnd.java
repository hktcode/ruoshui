package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleActionEnd extends SimpleAction
{
    public static SimpleActionEnd of()
    {
        return new SimpleActionEnd();
    }

    protected SimpleActionEnd()
    {
    }
}
