package com.hktcode.simple;

public class SimpleFinish implements SimpleActionEnd
{
    public static SimpleFinish of()
    {
        return new SimpleFinish();
    }

    private SimpleFinish()
    {
    }
}
