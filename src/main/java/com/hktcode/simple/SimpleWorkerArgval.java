package com.hktcode.simple;

import com.hktcode.jackson.JacksonObject;

public interface SimpleWorkerArgval<A extends SimpleWorkerArgval<A, G>, G extends SimpleWorkerGauges>
        extends JacksonObject
{
    long LOG_DURATION = 5 * 60 * 1000;

    SimpleWkstepAction<A, G> action();
}
