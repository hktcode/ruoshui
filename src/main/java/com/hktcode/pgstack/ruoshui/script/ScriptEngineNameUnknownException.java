/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.script;

import com.hktcode.lang.exception.ArgumentNullException;

public class ScriptEngineNameUnknownException extends RuntimeException
{
    public ScriptEngineNameUnknownException(String engineName)
    {
        super(String.format("unknown script engine name: engineName=%s", (engineName == null ? "" : engineName)));
        if (engineName == null) {
            throw new ArgumentNullException("engineName");
        }
        this.engineName = engineName;
    }

    public final String engineName;
}
