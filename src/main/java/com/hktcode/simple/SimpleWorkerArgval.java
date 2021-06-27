package com.hktcode.simple;

import com.hktcode.jackson.JacksonObject;

public interface SimpleWorkerArgval<A extends SimpleWorkerArgval<A>>
        extends JacksonObject
{
    long LOG_DURATION = 5 * 60 * 1000;

    SimpleWkstepAction<A> action();
}
