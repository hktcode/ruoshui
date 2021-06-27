package com.hktcode.simple;

import com.hktcode.jackson.JacksonObject;

public interface SimpleWorkerArgval extends JacksonObject
{
    long LOG_DURATION = 5 * 60 * 1000;

    SimpleWkstepAction action();
}
