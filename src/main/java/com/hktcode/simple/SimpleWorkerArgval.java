package com.hktcode.simple;

import com.hktcode.jackson.JacksonObject;

public interface SimpleWorkerArgval<A extends SimpleWorkerArgval<A, G>, G extends SimpleWorkerGauges>
        extends JacksonObject
{
    SimpleWkstepAction<A, G> action();
}
