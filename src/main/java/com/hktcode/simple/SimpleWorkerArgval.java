package com.hktcode.simple;

import com.hktcode.jackson.JacksonObject;

public interface SimpleWorkerArgval<A extends SimpleWorkerArgval<A, M>, M extends SimpleWorkerGauges>
        extends JacksonObject
{
    SimpleWkstepAction<A, M> action();
}
