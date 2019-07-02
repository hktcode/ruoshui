/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.script;

import com.hktcode.lang.exception.ArgumentNullException;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class NashornScript extends Jsr223BasicScript
{
    public static NashornScript of(String scriptText) throws ScriptException
    {
        if (scriptText == null) {
            throw new ArgumentNullException("scriptText");
        }
        return new NashornScript(scriptText);
    }

    public static final Logger logger = LoggerFactory.getLogger(NashornScript.class);

    public static final String ENGINE_NAME = "nashorn";

    public static final NashornScriptEngine ENGINE = (NashornScriptEngine) (new ScriptEngineManager().getEngineByName(ENGINE_NAME));

    private NashornScript(String scriptText) throws ScriptException
    {
        super(scriptText, ENGINE.compile(scriptText));
    }

    @Override
    public String getEngineName()
    {
        return ENGINE_NAME;
    }

    @Override
    public <D> Object eval(D dollar) throws ScriptException
    {
        if (dollar == null) {
            throw new ArgumentNullException("dollar");
        }
        return super.eval(ENGINE, dollar, logger);
    }
}
